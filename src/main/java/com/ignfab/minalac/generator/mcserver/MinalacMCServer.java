package com.ignfab.minalac.generator.mcserver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import com.ignfab.minalac.generator.MinalacGeneratorCLI;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.ParseException;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.ConditionalPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.DiscardPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.IdentityPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.JTSGeometryBufferPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataCopyPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataDefaultPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataParsePostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.GeoPackageProviderParams;
import com.ignfab.minalac.generator.parameters.providers.GeoTiffProviderParams;
import com.ignfab.minalac.generator.parameters.providers.ShapefileProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WFSProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WMSFloatBilProviderParams;
import com.ignfab.minalac.generator.parameters.tasks.CopyHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.FetchDataTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.LevelGroundTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.PopulateHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderBuildingsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderVectorsTaskParams;
import com.ignfab.minalac.generator.utils.execution.ScheduledTask;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class MinalacMCServer {
    public static void main(String[] args) {
        // Parse args and prepare generation
        Generation generation;
        try {
            generation = setupGeneration(args);
        } catch (ParseException | JsonProcessingException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Set up the instance
        InstanceContainer instanceContainer = setupInstance(generation);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(-5600, 50, -1000));
        });
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.setGameMode(GameMode.SPECTATOR);
            player.setFlying(true);
        });
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            Component msg = Component.translatable("multiplayer.disconnect.server_shutdown");
            ConnectionManager manager = MinecraftServer.getConnectionManager();
            Stream.concat(
                manager.getConfigPlayers().stream(),
                manager.getOnlinePlayers().stream()
            ).forEach(player -> player.kick(msg));
            // Wait for the players to receive the kick packet
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }

    private static @NotNull InstanceContainer setupInstance(Generation generation) {
        // Create the instance: Custom dimension to increase height and freeze time
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        DimensionType dimension = DimensionType.builder()
            .height(448)
            .fixedTime(6000L)
            .build();
        RegistryKey<DimensionType> dimensionKey = MinecraftServer.getDimensionTypeRegistry().register("minalac", dimension);
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(dimensionKey, IChunkLoader.noop());

        // Set the ChunkGenerator: Create a new tile for every generation request
        ScheduledTask.verbose = false;
        List<GeneratedChunkInfo> generatedChunks = new ArrayList<>();
        instanceContainer.setGenerator(unit -> {
            Instant start = Instant.now();
            WorldBBox3d bbox = new WorldBBox3d(
                // X/Z/-Y => X/Y/Z
                new WorldCoords3d(unit.absoluteStart().blockX(), -unit.absoluteStart().blockZ() - 1, unit.absoluteStart().blockY()),
                new WorldCoords3d(unit.absoluteEnd().blockX() - 1, -unit.absoluteEnd().blockZ(), unit.absoluteEnd().blockY()) // .absoluteEnd() is exclusive
            );
            GenerationTile tile = new GenerationTile(generation, bbox);
            ((MCServerTile) tile.voxels()).setUnit(unit);
            Scheduler<GenerationTile> scheduler = generation.scheduler().copy(Executors.newVirtualThreadPerTaskExecutor());
            boolean success = false;
            try {
                scheduler.run(tile, 30, TimeUnit.SECONDS);
                success = true;
            } catch (Throwable e) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                instanceContainer.sendMessage(Component.text("Chunk generation failed after %.2fs: (%d, %d, %d) => (%d, %d, %d)".formatted(
                    Duration.between(start, Instant.now()).toMillis() / 1000d,
                    bbox.minX(), bbox.minY(), bbox.minZ(),
                    bbox.maxX(), bbox.maxY(), bbox.maxZ()
                ), NamedTextColor.RED).hoverEvent(Component.text(writer.toString().replaceAll("\r", "").replaceAll("\t", "  ").trim(), NamedTextColor.GRAY)));
                MinecraftServer.getExceptionManager().handleException(e);

                // Stupid one-time retry strategy
                unit.modifier().fill(Block.AIR);
                try {
                    scheduler.run(tile, 30, TimeUnit.SECONDS);
                    success = true;
                    instanceContainer.sendMessage(Component.text("Retry successful", NamedTextColor.DARK_GREEN));
                } catch (Throwable e2) {
                    instanceContainer.sendMessage(Component.text("Retry failed", NamedTextColor.DARK_RED));
                }
            } finally {
                scheduler.shutdown();
                Instant end = Instant.now();
                Duration time = Duration.between(start, end);
                generatedChunks.add(new GeneratedChunkInfo(bbox, success, start, end, time));
                long millis = time.toMillis();
                System.out.printf("[%TT] Generated tile (%d, %d, %d) => (%d, %d, %d) in %dms%s%n", LocalDateTime.now(), bbox.minX(), bbox.minY(), bbox.minZ(), bbox.maxX(), bbox.maxY(), bbox.maxZ(), millis, success ? "" : " (failure)");
            }
        });

        // Set the ChunkSupplier: Computes light
        instanceContainer.setChunkSupplier(LightingChunk::new);

        // Schedule auto-save on stop: Saves generated world before exiting if enabled
        String destination = ((MCServerWorld) generation.world()).getDestination();
        boolean isSaving = destination != null;
        if (isSaving) {
            instanceContainer.setChunkLoader(new AnvilLoader(destination));
            MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
                try {
                    instanceContainer.saveChunksToStorage().get();
                } catch (InterruptedException | ExecutionException e) {
                    MinecraftServer.getExceptionManager().handleException(e);
                }
            });
        }

        // Schedule auto-save & unload periodically: Frees memory by unloading unused chunks
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            List<IntIntImmutablePair> toUnload = instanceContainer.getChunks().stream()
                .filter(chunk -> chunk.isLoaded() && chunk.getViewers().isEmpty())
                .map(chunk -> IntIntImmutablePair.of(chunk.getChunkX(), chunk.getChunkZ()))
                .toList();
            (isSaving ? instanceContainer.saveChunksToStorage() : CompletableFuture.completedFuture(null)).thenRun(() -> {
                for (IntIntImmutablePair key : toUnload) {
                    Chunk chunk = instanceContainer.getChunk(key.leftInt(), key.rightInt());
                    if (chunk != null && chunk.isLoaded() && chunk.getViewers().isEmpty())
                        instanceContainer.unloadChunk(chunk);
                }
            });
            return TaskSchedule.seconds(30);
        }, ExecutionType.TICK_END);

        // Create an info bar: Displays performances in real time
        BossBar bar = BossBar.bossBar(Component.empty(), 1, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
        bar.addViewer(instanceContainer);
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            int success = 0;
            int failed = 0;
            long timeLast30s = 0;
            int totalLast30s = 0;
            Instant t = Instant.now().minusSeconds(30);
            for (GeneratedChunkInfo generatedChunk : new ArrayList<>(generatedChunks)) {
                if (generatedChunk.success())
                    success++;
                else
                    failed++;
                if (generatedChunk.end().isAfter(t)) {
                    timeLast30s += generatedChunk.time().toMillis();
                    totalLast30s++;
                }
            }
            long avgLast30s = totalLast30s == 0 ? 0 : timeLast30s / totalLast30s;
            double chunksPerSecond = totalLast30s / 30d;
            bar.name(Component.join(JoinConfiguration.separator(Component.text(" | ", NamedTextColor.GRAY)),
                Component.text("Avg. gen. time (last 30s): %dms (%d chunk%s)".formatted(avgLast30s, totalLast30s, s(totalLast30s))),
                Component.text("Estimated: %.1f chunk%s/s".formatted(chunksPerSecond, s(chunksPerSecond))),
                Component.text("Generated chunk%s: %d".formatted(s(success), success), NamedTextColor.GREEN),
                Component.text("Failed chunk%s: %d".formatted(s(failed), failed), NamedTextColor.RED)));
            return TaskSchedule.nextTick();
        });

        return instanceContainer;
    }

    private static @NotNull String s(double v) {
        return v < 2 ? "" : "s";
    }

    private record GeneratedChunkInfo(WorldBBox3d bbox, boolean success, Instant start, Instant end, Duration time) {}

    private static @NotNull Generation setupGeneration(String[] args) throws ParseException, JsonProcessingException {
        HttpTrustAllSSL.applyGlobally();

        MinalacGeneratorCLI cli = new MinalacGeneratorCLI();
        cli.parse(args);

        String destination = cli.saveDisabled() ? null : cli.outputPath().toFile().getName();

        ParamsParser parser = new ParamsParser();

        parser.registerFormat("minecraft", new OutputFormat(() -> new MCServerWorld(destination), MCServerBlockParams.class, MCServerBlockParams::new));

        parser.registerParams("copyHeightmap", CopyHeightmapTaskParams.class);
        parser.registerParams("fetchData", FetchDataTaskParams.class);
        parser.registerParams("levelGround", LevelGroundTaskParams.class);
        parser.registerParams("populateHeightmap", PopulateHeightmapTaskParams.class);
        parser.registerParams("renderBuildings", RenderBuildingsTaskParams.class);
        parser.registerParams("renderHeightmap", RenderHeightmapTaskParams.class);
        parser.registerParams("renderVectors", RenderVectorsTaskParams.class);

        parser.registerParams("wfs", WFSProviderParams.class);
        parser.registerParams("gpkg", GeoPackageProviderParams.class);
        parser.registerParams("shapefile", ShapefileProviderParams.class);
        parser.registerParams("wmsFloat", WMSFloatBilProviderParams.class);
        parser.registerParams("geotiff", GeoTiffProviderParams.class);

        parser.registerParams("floatMatrix", FloatMatrixProcessorParams.class);
        parser.registerParams("geoToolsVector", GeoToolsVectorProcessorParams.class);

        parser.registerParams("identity", IdentityPostProcessorParams.class);
        parser.registerParams("discard", DiscardPostProcessorParams.class);
        parser.registerParams("conditional", ConditionalPostProcessorParams.class);
        parser.registerParams("copy", MetadataCopyPostProcessorParams.class);
        parser.registerParams("default", MetadataDefaultPostProcessorParams.class);
        parser.registerParams("parse", MetadataParsePostProcessorParams.class);
        parser.registerParams("geometryBuffer", JTSGeometryBufferPostProcessorParams.class);

        return parser.parse(cli.readParameters()).create(null);
    }
}

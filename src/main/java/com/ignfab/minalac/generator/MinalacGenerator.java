package com.ignfab.minalac.generator;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.geotools.api.referencing.FactoryException;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.modules.luanti.LuantiOutputModule;
import com.ignfab.minalac.generator.modules.minecraft.MinecraftOutputModule;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.ParseException;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.OsmProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.ConditionalPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.DiscardPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.IdentityPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.JTSGeometryBufferPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataCopyPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataDefaultPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataParsePostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataTruncatePostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataValueMappingPostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.GeoPackageProviderParams;
import com.ignfab.minalac.generator.parameters.providers.GeoTiffProviderParams;
import com.ignfab.minalac.generator.parameters.providers.OverpassProviderParams;
import com.ignfab.minalac.generator.parameters.providers.ShapefileProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WFSProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WMSFloatBilProviderParams;
import com.ignfab.minalac.generator.parameters.tasks.CopyHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.FetchDataTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.FillBetweenHeightmapAndMetadataTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.HeightmapStatsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.NoOperationTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.PlaceTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.PopulateHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderBuildingsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderLines2dTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderLinesTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderPoints2dTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderPointsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderSurfacesTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.ScheduleTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.SequenceTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.SetSpawnTaskParams;
import com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d.Voxelizer2dParams;
import com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d.Voxelizer3dParams;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.execution.TaskFailedException;
import com.ignfab.minalac.generator.utils.modules.ModulesLoader;
import com.ignfab.minalac.generator.utils.modules.ModulesManager;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;

/**
 * Main class of Minalac project.
 */
public final class MinalacGenerator {
    private MinalacGenerator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Serves as the entry point for the program.
     *
     * @param args command line arguments
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public static void main(String[] args) throws FactoryException, InterruptedException, MapWriteException, ParseException, TaskFailedException, TransformException, TimeoutException, GenerationFailedException {
        // Execution duration start
        Instant start = Instant.now();
        HttpTrustAllSSL.applyGlobally();

        // Deserialization duration start
        Instant initializationStart = Instant.now();
        // Command line arguments parsing & basic processing
        MinalacGeneratorCLI cli = new MinalacGeneratorCLI();
        cli.parse(args);

        File destination;
        String parameters = cli.readParameters();
        if (cli.saveDisabled())
            destination = null;
        else {
            destination = cli.outputPath().toFile();
            // Write the parameters file in the world root directory
            try {
                FileHelpers.write(new File(destination, "parameters.yaml"), parameters);
            } catch (IOException e) {
                throw new MapWriteException("Failed to write parameters.yaml", e);
            }
        }

        Integer maxTileSize = cli.maxTileSize();

        ModulesLoader loader = new ModulesLoader();

        // Built-in modules
        loader.add(new LuantiOutputModule());
        loader.add(new MinecraftOutputModule());

        // External modules
        if (cli.modulesPath() != null)
            loader.loadModulesDirectory(cli.modulesPath().toFile());

        ModulesManager modules = loader.create();

        // Generation parsing
        ParamsParser parser = new ParamsParser();

        // TODO: Static method that provides a ParamsParser with all default renderers
        // If those name values are modified, update the documentation accordingly
        parser.registerParams("noOperation", NoOperationTaskParams.class);
        parser.registerParams("sequence", SequenceTaskParams.class);
        parser.registerParams("schedule", ScheduleTaskParams.class);
        parser.registerParams("copyHeightmap", CopyHeightmapTaskParams.class);
        parser.registerParams("computeHeightmapStats", HeightmapStatsTaskParams.class);
        parser.registerParams("fetchData", FetchDataTaskParams.class);
        parser.registerParams("fillBetweenHeightmapAndMetadata", FillBetweenHeightmapAndMetadataTaskParams.class);
        parser.registerParams("place", PlaceTaskParams.class);
        parser.registerParams("populateHeightmap", PopulateHeightmapTaskParams.class);
        parser.registerParams("renderBuildings", RenderBuildingsTaskParams.class);
        parser.registerParams("renderHeightmap", RenderHeightmapTaskParams.class);
        parser.registerParams("renderSurfaces", RenderSurfacesTaskParams.class);
        parser.registerParams("renderLines", RenderLinesTaskParams.class);
        parser.registerParams("renderLines2d", RenderLines2dTaskParams.class);
        parser.registerParams("renderPoints", RenderPointsTaskParams.class);
        parser.registerParams("renderPoints2d", RenderPoints2dTaskParams.class);
        parser.registerParams("setSpawn", SetSpawnTaskParams.class);

        parser.registerParams("wfs", WFSProviderParams.class);
        parser.registerParams("gpkg", GeoPackageProviderParams.class);
        parser.registerParams("shapefile", ShapefileProviderParams.class);
        parser.registerParams("wmsFloat", WMSFloatBilProviderParams.class);
        parser.registerParams("geotiff", GeoTiffProviderParams.class);
        parser.registerParams("overpass", OverpassProviderParams.class);

        parser.registerParams("floatMatrix", FloatMatrixProcessorParams.class);
        parser.registerParams("geoToolsVector", GeoToolsVectorProcessorParams.class);
        parser.registerParams("osm", OsmProcessorParams.class);

        parser.registerParams("identity", IdentityPostProcessorParams.class);
        parser.registerParams("discard", DiscardPostProcessorParams.class);
        parser.registerParams("conditional", ConditionalPostProcessorParams.class);
        parser.registerParams("copy", MetadataCopyPostProcessorParams.class);
        parser.registerParams("default", MetadataDefaultPostProcessorParams.class);
        parser.registerParams("parse", MetadataParsePostProcessorParams.class);
        parser.registerParams("truncate", MetadataTruncatePostProcessorParams.class);
        parser.registerParams("geometryBuffer", JTSGeometryBufferPostProcessorParams.class);
        parser.registerParams("remap", MetadataValueMappingPostProcessorParams.class);

        Voxelizer2dParams.register(parser);
        Voxelizer3dParams.register(parser);

        modules.registerParams(parser);

        Generation generation = parser.parse(parameters).create(destination, maxTileSize);

        System.out.printf("Generation initialization took %ds.%n", Duration.between(initializationStart, Instant.now()).toSeconds());
        if (cli.generationDisabled()) {
            System.out.printf("Total: %ds.%nDone (stopped before map generation).%n", Duration.between(start, Instant.now()).toSeconds());
            return;
        }

        Instant worldInitializationStart = Instant.now();

        // Initialize world
        generation.world().initialize();
        System.out.printf("World initialization took %ds.%n", Duration.between(worldInitializationStart, Instant.now()).toSeconds());

        int numberOfTiles = generation.numberOfTiles();
        System.out.printf("Generation will be performed in %d tiles of maximum %d voxels by side.%n", numberOfTiles, generation.maxTileSize());

        // Start generating tiles
        Duration generatingDuration = Duration.ZERO; // This will hold total generation time
        Duration mapSavingDuration = Duration.ZERO; // This will hold total map saving time

        int currentTile = 0;

        try {
            while (generation.nextTile()) {
                currentTile++;
                WorldBBox3d limits = GenerationTile.current().limits();
                String tileString = "%d/%d (x=%d..%d, y=%d..%d)".formatted(currentTile, numberOfTiles, limits.minX(), limits.maxX(), limits.minY(), limits.maxY());
                System.out.printf("%nTile %s.%n", tileString);

                // Generate tile
                Instant tileGenerationStart = Instant.now();

                generation.forEachTileScheduler().run(5, TimeUnit.MINUTES);
                Duration tileGenerationDuration = Duration.between(tileGenerationStart, Instant.now());

                generatingDuration = generatingDuration.plus(tileGenerationDuration);
                System.out.printf("Tile %s generated in %ds.%n", tileString, tileGenerationDuration.toSeconds());

                // Save tile
                Instant tileSavingStart = Instant.now();
                GenerationTile.current().save();
                Duration tileSavingDuration = Duration.between(tileSavingStart, Instant.now());

                mapSavingDuration = mapSavingDuration.plus(tileSavingDuration);
                System.out.printf("Tile %s saved in %ds.%n", tileString, tileSavingDuration.toSeconds());
            }
        } finally {
            generation.forEachTileScheduler().shutdown();
        }

        System.out.printf("%nAll %d tiles generated and saved.%nSpent %ds generating and %ds saving.%n", numberOfTiles, generatingDuration.toSeconds(), mapSavingDuration.toSeconds());

        try {
            Instant afterAllTilesStart = Instant.now();
            generation.afterAllTilesScheduler().run(5, TimeUnit.MINUTES);
            Duration afterAllTilesDuration = Duration.between(afterAllTilesStart, Instant.now());
            System.out.printf("Tasks executed after all tiles took %ds.%n", afterAllTilesDuration.toSeconds());
        } finally {
            generation.afterAllTilesScheduler().shutdown();
        }

        Instant finalizationStart = Instant.now();
        generation.world().finalizeAndSave();
        System.out.printf("Generation finalization took %ds.%n", Duration.between(finalizationStart, Instant.now()).toSeconds());
        System.out.printf("Total: %ds.%nDone.%n", Duration.between(start, Instant.now()).toSeconds());
    }
}

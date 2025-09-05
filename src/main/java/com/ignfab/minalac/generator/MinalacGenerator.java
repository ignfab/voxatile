package com.ignfab.minalac.generator;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geotools.api.referencing.FactoryException;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.ParseException;
import com.ignfab.minalac.generator.parameters.placeables.voxels.MCVoxelParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.MTVoxelParams;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.IntegerMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.ConditionalPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.DiscardPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.IdentityPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.JTSGeometryBufferPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataCopyPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataDefaultPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataParsePostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataValueMappingPostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.GeoPackageProviderParams;
import com.ignfab.minalac.generator.parameters.providers.GeoTiffProviderParams;
import com.ignfab.minalac.generator.parameters.providers.ShapefileProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WFSProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WMSFloatBilProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WMSImageProviderParams;
import com.ignfab.minalac.generator.parameters.tasks.CopyHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.FetchDataTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.LevelGroundTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.PopulateHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderBuildingsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderHeightmapTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderVectorsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.SetSpawnTaskParams;
import com.ignfab.minalac.generator.utils.execution.TaskFailedException;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

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
     * @throws JsonProcessingException
     */
    public static void main(String[] args) throws FactoryException, InterruptedException, MapWriteException, ParseException, TaskFailedException, TransformException, JsonProcessingException, TimeoutException, GenerationFailedException {
        // Execution duration start
        Instant start = Instant.now();
        HttpTrustAllSSL.applyGlobally();

        // Deserialization duration start
        Instant initializationStart = Instant.now();
        // Command line arguments parsing & basic processing
        MinalacGeneratorCLI cli = new MinalacGeneratorCLI();
        cli.parse(args);
        File destination;

        if (cli.saveDisabled())
            destination = null;
        else
            destination = cli.outputPath().toFile();

        Integer maxTileSize = cli.maxTileSize();

        // Generation parsing
        ParamsParser parser = new ParamsParser();

        // Register game formats
        parser.registerFormat("minecraft", new OutputFormat(() -> new MCVoxelWorld(destination), MCVoxelParams.class, MCVoxelParams::new));
        parser.registerFormat("minetest", new OutputFormat(() -> new MTVoxelWorld(destination), MTVoxelParams.class, MTVoxelParams::new));

        // TODO: Static method that provides a ParamsParser with all default renderers
        // If those name values are modified, update the documentation accordingly
        parser.registerParams("copyHeightmap", CopyHeightmapTaskParams.class);
        parser.registerParams("fetchData", FetchDataTaskParams.class);
        parser.registerParams("levelGround", LevelGroundTaskParams.class);
        parser.registerParams("populateHeightmap", PopulateHeightmapTaskParams.class);
        parser.registerParams("renderBuildings", RenderBuildingsTaskParams.class);
        parser.registerParams("renderHeightmap", RenderHeightmapTaskParams.class);
        parser.registerParams("renderVectors", RenderVectorsTaskParams.class);
        parser.registerParams("setSpawn", SetSpawnTaskParams.class);

        parser.registerParams("wfs", WFSProviderParams.class);
        parser.registerParams("gpkg", GeoPackageProviderParams.class);
        parser.registerParams("shapefile", ShapefileProviderParams.class);
        parser.registerParams("wmsFloat", WMSFloatBilProviderParams.class);
        parser.registerParams("wmsImage", WMSImageProviderParams.class);
        parser.registerParams("geotiff", GeoTiffProviderParams.class);

        parser.registerParams("integerMatrix", IntegerMatrixProcessorParams.class);
        parser.registerParams("floatMatrix", FloatMatrixProcessorParams.class);
        parser.registerParams("geoToolsVector", GeoToolsVectorProcessorParams.class);

        parser.registerParams("identity", IdentityPostProcessorParams.class);
        parser.registerParams("discard", DiscardPostProcessorParams.class);
        parser.registerParams("conditional", ConditionalPostProcessorParams.class);
        parser.registerParams("copy", MetadataCopyPostProcessorParams.class);
        parser.registerParams("default", MetadataDefaultPostProcessorParams.class);
        parser.registerParams("parse", MetadataParsePostProcessorParams.class);
        parser.registerParams("geometryBuffer", JTSGeometryBufferPostProcessorParams.class);
        parser.registerParams("remap", MetadataValueMappingPostProcessorParams.class);

        Generation generation = parser.parse(cli.readParameters()).create(maxTileSize);

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
            for (GenerationTile tile : generation.tiles()) {
                currentTile++;
                String tileString = "%d/%d (x=%d..%d, y=%d..%d)".formatted(currentTile, numberOfTiles, tile.limits().minX(), tile.limits().maxX(), tile.limits().minY(), tile.limits().maxY());
                System.out.printf("%nTile %s.%n", tileString);

                // Generate tile
                Instant tileGenerationStart = Instant.now();
                generation.scheduler().run(tile, 5, TimeUnit.MINUTES);
                Duration tileGenerationDuration = Duration.between(tileGenerationStart, Instant.now());

                generatingDuration = generatingDuration.plus(tileGenerationDuration);
                System.out.printf("Tile %s generated in %ds.%n", tileString, tileGenerationDuration.toSeconds());

                // Save tile
                Instant tileSavingStart = Instant.now();
                tile.save();
                Duration tileSavingDuration = Duration.between(tileSavingStart, Instant.now());

                mapSavingDuration = mapSavingDuration.plus(tileSavingDuration);
                System.out.printf("Tile %s saved in %ds.%n", tileString, tileSavingDuration.toSeconds());
            }
        } finally {
            generation.scheduler().shutdown();
        }

        System.out.printf("%nAll %d tiles generated and saved.%nSpent %ds generating and %ds saving.%n", numberOfTiles, generatingDuration.toSeconds(), mapSavingDuration.toSeconds());

        Instant finalizationStart = Instant.now();

        VoxelWorldMetadata metadata = generation.world().getMetadata();
        metadata.setWorldName("Minalac");

        generation.world().finalizeAndSave();
        System.out.printf("Generation finalization took %ds.%n", Duration.between(finalizationStart, Instant.now()).toSeconds());
        System.out.printf("Total: %ds.%nDone.%n", Duration.between(start, Instant.now()).toSeconds());
    }
}

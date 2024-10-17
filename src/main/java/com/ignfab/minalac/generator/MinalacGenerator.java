package com.ignfab.minalac.generator;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geotools.api.referencing.FactoryException;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.ParseException;
import com.ignfab.minalac.generator.parameters.placeables.voxels.MCVoxelTypeParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.MTVoxelTypeParams;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.IdentityPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.JTSGeometryBufferPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataCopyPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataDefaultPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataParsePostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.GeoPackageProviderParams;
import com.ignfab.minalac.generator.parameters.providers.ShapefileProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WFSProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WMSFloatBilProviderParams;
import com.ignfab.minalac.generator.parameters.renderers.BuildingRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.CopyHeightmapRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.HeightmapRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.LevelingRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.MatrixToHeightmapRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.VectorRendererParams;
import com.ignfab.minalac.generator.utils.execution.TaskFailedException;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
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
    public static void main(String[] args) throws FactoryException, InterruptedException, MapWriteException, ParseException, TaskFailedException, TransformException, JsonProcessingException {
        // Execution duration start
        Instant start = Instant.now();
        HttpTrustAllSSL.applyGlobally();

        // Deserialization duration start
        Instant initializationStart = Instant.now();
        // Command line arguments parsing & basic processing
        MinalacGeneratorCLI cli = new MinalacGeneratorCLI();
        cli.parse(args);

        // Generation parsing
        ParamsParser parser = new ParamsParser();

        // Register game formats
        parser.registerFormat("minecraft", new OutputFormat(MCVoxelWorld::new, MCVoxelTypeParams.class, MCVoxelTypeParams::new));
        parser.registerFormat("minetest", new OutputFormat(MTVoxelWorld::new, MTVoxelTypeParams.class, MTVoxelTypeParams::new));

        // TODO: Static method that provides a ParamsParser with all default renderers
        // If those name values are modified, update the documentation accordingly
        parser.registerParams("matrixToHeightmap", MatrixToHeightmapRendererParams.class);
        parser.registerParams("heightmapRenderer", HeightmapRendererParams.class);
        parser.registerParams("vector", VectorRendererParams.class);
        parser.registerParams("leveling", LevelingRendererParams.class);
        parser.registerParams("building", BuildingRendererParams.class);

        parser.registerParams("copyHeightmap", CopyHeightmapRendererParams.class);

        parser.registerParams("wfs", WFSProviderParams.class);
        parser.registerParams("gpkg", GeoPackageProviderParams.class);
        parser.registerParams("shapefile", ShapefileProviderParams.class);
        parser.registerParams("wmsFloat", WMSFloatBilProviderParams.class);

        parser.registerParams("floatMatrix", FloatMatrixProcessorParams.class);
        parser.registerParams("geoToolsVector", GeoToolsVectorProcessorParams.class);

        parser.registerParams("identity", IdentityPostProcessorParams.class);
        parser.registerParams("copy", MetadataCopyPostProcessorParams.class);
        parser.registerParams("default", MetadataDefaultPostProcessorParams.class);
        parser.registerParams("parse", MetadataParsePostProcessorParams.class);
        parser.registerParams("geometryBuffer", JTSGeometryBufferPostProcessorParams.class);

        Generation generation = parser.parse(cli.readParameters()).create();

        System.out.println("Initialization: " + Duration.between(initializationStart, Instant.now()).toSeconds() + "s");
        if (cli.generationDisabled()) {
            System.out.println("Total: " + Duration.between(start, Instant.now()).toSeconds() + "s");
            System.out.println("Done (stopped before map generation)");
            return;
        }

        System.out.println("Creation of the map.");
        // Start generation duration
        Instant generationStart = Instant.now();
        generation.scheduler().start();
        try {
            generation.scheduler().waitUntilAllTasksFinished(5, TimeUnit.MINUTES);
        } finally {
            generation.scheduler().shutdown();
        }

        System.out.println("Generation: " + Duration.between(generationStart, Instant.now()).toSeconds() + "s");
        if (cli.saveDisabled()) {
            System.out.println("Total: " + Duration.between(start, Instant.now()).toSeconds() + "s");
            System.out.println("Done (stopped before saving map)");
            return;
        }

        System.out.println("Saving world");
        VoxelWorldMetadata metadata = generation.world().getMetadata();
        WorldBBox3d limits = generation.world().limits();
        int spawnX = (limits.minX() + limits.maxX() + 1) / 2;
        int spawnY = (limits.minY() + limits.maxY() + 1) / 2;
        metadata.setSpawn(new WorldCoords3d(spawnX, spawnY, generation.heightmaps().get("ground").get(spawnX, spawnY) + 1));
        metadata.setWorldName("Minalac");

        File directory = cli.outputPath().toFile();
        generation.world().save(directory);
        System.out.println("Total: " + Duration.between(start, Instant.now()).toSeconds() + "s");
        System.out.println("Done");
    }
}

package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.ParseException;
import com.ignfab.minalac.generator.parameters.placeables.minecraft.MCVoxelTypeParams;
import com.ignfab.minalac.generator.parameters.placeables.minetest.MTVoxelTypeParams;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataCopyPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataDefaultPostProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.post.MetadataParsePostProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.WFSProviderParams;
import com.ignfab.minalac.generator.parameters.providers.WMSFloatBilProviderParams;
import com.ignfab.minalac.generator.parameters.renderers.GroundRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.HeightmapRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.VectorRendererParams;
import com.ignfab.minalac.generator.utils.execution.TaskFailedException;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

import org.geotools.api.referencing.FactoryException;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * This is a temporary class to have an idea of how the program works.
 * It generates a Minetest map which is a 3D rendering from a heightmap
 */
public final class SampleImplementation {
    private SampleImplementation() {
        throw new UnsupportedOperationException();
    }

    /**
     * Serves as the entry point for the program.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws FactoryException, InterruptedException, MapWriteException, ParseException, TaskFailedException, TransformException {
        long start = System.currentTimeMillis();
        HttpTrustAllSSL.applyGlobally();

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
        parser.registerParams("heightmap", HeightmapRendererParams.class);
        parser.registerParams("ground", GroundRendererParams.class);
        parser.registerParams("vector", VectorRendererParams.class);

        parser.registerParams("wfs", WFSProviderParams.class);
        parser.registerParams("wmsFloat", WMSFloatBilProviderParams.class);

        parser.registerParams("floatMatrix", FloatMatrixProcessorParams.class);
        parser.registerParams("geoToolsVector", GeoToolsVectorProcessorParams.class);

        parser.registerParams("copy", MetadataCopyPostProcessorParams.class);
        parser.registerParams("parse", MetadataParsePostProcessorParams.class);
        parser.registerParams("default", MetadataDefaultPostProcessorParams.class);

        Generation generation = parser.parse(cli.readParameters()).create();

        System.out.println("Creation of the map.");
        generation.scheduler().start();

        try {
            generation.scheduler().waitUntilAllTasksFinished(5, TimeUnit.MINUTES);
        } finally {
            generation.scheduler().shutdown();
        }

        System.out.println("Saving world");
        VoxelWorldMetadata metadata = generation.world().getMetadata();
        WorldBBox3d limits = generation.world().limits();
        int spawnX = (limits.minX() + limits.maxX() + 1) / 2;
        int spawnY = (limits.minY() + limits.maxY() + 1) / 2;
        metadata.setSpawn(new WorldCoords3d(spawnX, spawnY, generation.heightmaps().get("ground").get(spawnX, spawnY) + 1));
        metadata.setWorldName("Minalac");

        File directory = cli.getOutputPath().toFile();

        if (directory.exists()) {
            purgeDirectory(directory);
        } else {
            if (!directory.mkdirs())
                throw new MapWriteException("Cannot generate the map because the folder " + directory.getAbsolutePath() + " cannot be created");
        }

        generation.world().save(directory);
        System.out.println("Done");

        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start) / 1000 + "s");
    }

    private static void purgeDirectory(File directoryToPurge) throws MapWriteException {
        File[] files = directoryToPurge.listFiles();
        if (files == null)
            throw new MapWriteException("Unable to process content of: " + directoryToPurge + ". Is it a writable directory?");
        for (File file : files) {
            if (file.isDirectory())
                purgeDirectory(file);
            if (!file.delete())
                throw new MapWriteException("Failed to delete " + file);
        }
    }
}

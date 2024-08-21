package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.parsing.ParamsParser;
import com.ignfab.minalac.generator.parsing.ParseException;
import com.ignfab.minalac.generator.processors.GeoToolsVectorProcessor;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.post.MetadataCopyPostProcessor;
import com.ignfab.minalac.generator.processors.post.MetadataParsePostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;
import com.ignfab.minalac.generator.renderers.VectorRenderer;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.execution.TaskFailedException;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

        // Generation parameters parsing
        ParamsParser parser = new ParamsParser(cli.readParameters());

        System.out.println("Creation of the map.");

        Generation generation = parser.createGeneration();

        String crsName = "EPSG:2154";
        CoordinateReferenceSystem crs = CRS.decode(crsName);
        Envelope envelope = generation.getEnvelopeForCRS(crs);
        String bboxURL = "BBOX=" + envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY();

        // Various data stores
        ModelStore store = new ModelStore();
        Heightmap groundHeightmap = generation.getHeightmap("ground");
        VoxelWorld world = parser.createVoxelWorld();

        Scheduler scheduler = new Scheduler();

        // Downloads
        scheduler.schedule("heightmaps.ground", () -> {
            log("Downloading heightmap");
            try {
                fillGroundHeightmap("https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES=&CRS=" + crsName + "&" + bboxURL, groundHeightmap, generation.getVerticalScale());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            log("Downloaded heightmap");
        });

        scheduler.schedule("models.buildings", () -> {
            log("Downloading buildings");
            CoordsConverter converter; // This is supposed to be the layer CRS (actually the same for this demo)
            try {
                converter = generation.makeCoordsConverter(crs);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            }
            retrieveDataAndFillModelStore(store, "building",
                new WFS1_1_GML3_1_DataProvider("https://data.geopf.fr/wfs/wfs?SERVICE=WFS&REQUEST=GetFeature&VERSION=2.0.0&TYPENAMES=BDTOPO_V3:batiment&STARTINDEX=0&COUNT=1000&SRSNAME=urn:ogc:def:crs:EPSG::2154&" + bboxURL + ",urn:ogc:def:crs:EPSG::2154&outputFormat=text%2Fxml%3B%20subtype%3Dgml%2F3.1.1"),
                new GeoToolsVectorProcessor(converter),
                new MetadataCopyPostProcessor("hauteur", "height", false, false),
                new MetadataParsePostProcessor<>(
                    "height",
                    Double.class,
                    obj -> {
                        if (obj instanceof Number number)
                            return number.doubleValue();
                        else if (obj instanceof String string)
                            return Double.parseDouble(string);
                        return null;
                    },
                    MetadataParsePostProcessor.ParsingFailurePolicy.REMOVE_METADATA,
                    false,
                    false
                ),
                // TODO Transform into something like "MetadataDefaultPostProcessor"
                new PostProcessor<>() {
                    @Override
                    public Class<? super Model> acceptedModelType() {
                        return Model.class;
                    }

                    @Override
                    public Class<? extends Model> processedModelType(Class<? extends Model> inputModelType) {
                        return inputModelType;
                    }

                    @Override
                    public Model process(Model model) {
                        if (!model.hasMetadata("height"))
                            model.setMetadata("height", 20d);
                        return model;
                    }
                },
                // TODO Remove once the value is really used (currently useful to simulate value usage and failure in case of incorrect value)
                new PostProcessor<>() {
                    @Override
                    public Class<? super Model> acceptedModelType() {
                        return Model.class;
                    }

                    @Override
                    public Class<? extends Model> processedModelType(Class<? extends Model> inputModelType) {
                        return inputModelType;
                    }

                    @Override
                    public Model process(Model model) throws GenerationFailedException {
                        Object height = model.getMetadata("height");
                        if (!(height instanceof Double))
                            throw new GenerationFailedException("Illegal height value: " + (height == null ? "null" : (height + " (" + height.getClass() + ")")));
                        return model;
                    }
                }
            );
            log("Downloaded buildings");
        });

        // Rendering
        scheduler.schedule("renderers.ground", () -> {
            log("Placing ground");
            placeVoxelFromHeightmap(groundHeightmap, world);
            log("Placed ground");
        }, "heightmaps.ground");

        scheduler.schedule("renderers.buildings", () -> {
            log("Placing buildings");
            new VectorRenderer(
                groundHeightmap,
                store.getByType("building"),
                world.getFactory().createVoxelType(SemanticType.COBBLE),
                world.getFactory().createVoxelType(SemanticType.BRICK)
            ).render(generation.getWorldBBox2d().to3d(-32_000, 64_000));
            log("Placed buildings");
        }, "heightmaps.ground", "models.buildings");

        scheduler.start();
        try {
            scheduler.waitUntilAllTasksFinished(5, TimeUnit.MINUTES);
        } finally {
            scheduler.shutdown();
        }

        System.out.println("Saving world");
        VoxelWorldMetadata metadata = world.getMetadata();
        metadata.setSpawn(new WorldCoords3d(0, 0, groundHeightmap.get(0, 0) + 1));
        metadata.setWorldName("Minalac");
        save(cli.getOutputPath().toFile(), world);

        System.out.println("Done");

        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start) / 1000 + "s");
    }

    private static void log(String message) {
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), message);
    }

    private static <T> void retrieveDataAndFillModelStore(ModelStore store, String type, Provider<T> provider, Processor<? super T, ?> processor, PostProcessor<?, ?>... postProcessors) {
        if (!processor.acceptedType().isAssignableFrom(provider.providedType()))
            throw new IllegalArgumentException("Processor cannot treat provided type. Provided = %s, Accepted = %s".formatted(provider.providedType(), processor.acceptedType()));
        Class<? extends Model> modelType = processor.modelType();
        for (PostProcessor<?, ?> postProcessor : postProcessors) {
            if (!postProcessor.acceptedModelType().isAssignableFrom(modelType))
                throw new IllegalArgumentException("PostProcessor cannot treat model type. Current model type = %s, Accepted model type = %s".formatted(modelType, postProcessor.acceptedModelType()));
            @SuppressWarnings("unchecked") // The model type has been validated above
            PostProcessor<Model, ?> uncheckedPostProcessor = (PostProcessor<Model, ?>) postProcessor;
            modelType = uncheckedPostProcessor.processedModelType(modelType);
        }
        try (Provider.Result<T> result = provider.provide()) {
            for (T data : result) {
                try {
                    Model model = processor.process(data);
                    for (PostProcessor<?, ?> postProcessor : postProcessors) {
                        if (model == null)
                            break;
                        @SuppressWarnings("unchecked") // All model types have been validated above
                        PostProcessor<Model, ?> uncheckedPostProcessor = (PostProcessor<Model, ?>) postProcessor;
                        model = uncheckedPostProcessor.process(model);
                    }
                    if (model != null)
                        store.add(type, model);
                } catch (IgnorableException e) {
                    // TODO Add an exception handling policy
                    // To fail even on ignorable exceptions:
                    // throw e;
                }
            }
        } catch (RetryableException e) {
            // TODO Implement a retry mechanism
            throw new RuntimeException(e);
        } catch (IOException | GenerationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void placeVoxelFromHeightmap(Heightmap map, VoxelWorld world) {
        VoxelType grassVT = world.getFactory().createVoxelType(SemanticType.GRASS);
        VoxelType stoneVT = world.getFactory().createVoxelType(SemanticType.STONE);
        VoxelType dirtVT = world.getFactory().createVoxelType(SemanticType.DIRT);

        for (WorldCoords2d coords : map.bbox()) {
            int x = coords.x();
            int y = coords.y();
            int z = map.get(coords);
            grassVT.place(x, y, z);
            dirtVT.place(x, y, (z - 1));
            dirtVT.place(x, y, (z - 2));

            for (int zStone = z - 3; zStone > z - (3 + 10); zStone--) {
                stoneVT.place(x, y, zStone);
            }
        }
    }

    private static void fillGroundHeightmap(String partialUrl, Heightmap heightmap, double verticalScale) throws MalformedURLException {
        int width = heightmap.bbox().sizeX();
        int height = heightmap.bbox().sizeY();
        URL url = new URL(partialUrl + "&WIDTH=" + width + "&HEIGHT=" + height);

        byte[] data;
        try (InputStream inputStream = url.openStream()) {
            int total = 0;
            int read;
            data = new byte[width * height * 4];
            while (0 < (read = inputStream.read(data, total, data.length - total)))
                total = total + read;
            if (total != data.length)
                throw new RuntimeException("Incomplete data read from response stream");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        float[] mntArray = byteArrayToFloatArray(data);

        int xMin = heightmap.bbox().minX();
        int yMin = heightmap.bbox().minY();

        int index = 0;

        for (int y = height - 1; y >= 0; y--) { // In raster, Y axis is downwards
            for (int x = 0; x < width; x++) {
                heightmap.set(x + xMin, y + yMin, (int) (mntArray[index] / verticalScale));
                index++;
            }
        }
    }

    private static float[] byteArrayToFloatArray(byte[] byteData) {
        float[] floatData = new float[byteData.length / 4];
        ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floatData);
        return floatData;
    }

    private static void save(File directory, VoxelWorld world) throws MapWriteException {
        deleteDirectory(directory);
        if (directory.mkdirs())
            world.save(directory);
        else
            throw new MapWriteException("Cannot generate the map because the folder " + directory.getAbsolutePath() + " cannot be created");
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents)
                deleteDirectory(file);
        }
        directoryToBeDeleted.delete();
    }
}

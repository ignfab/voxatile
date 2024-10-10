package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider;
import com.ignfab.minalac.generator.inputs.WMSFloatBilDataProvider;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.ParseException;
import com.ignfab.minalac.generator.parameters.renderers.HeightmapRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.NatureRendererParams;
import com.ignfab.minalac.generator.parameters.renderers.VectorRendererParams;
import com.ignfab.minalac.generator.processors.FloatMatrixProcessor;
import com.ignfab.minalac.generator.processors.GeoToolsVectorProcessor;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.post.MetadataCopyPostProcessor;
import com.ignfab.minalac.generator.processors.post.MetadataParsePostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;
import com.ignfab.minalac.generator.renderers.GroundRenderer;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.execution.TaskFailedException;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.SimpleVoxelPattern;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;

import java.io.File;
import java.io.IOException;
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
    @SuppressWarnings("MethodLength")
    public static void main(String[] args) throws FactoryException, InterruptedException, MapWriteException, ParseException, TaskFailedException, TransformException {
        long start = System.currentTimeMillis();
        HttpTrustAllSSL.applyGlobally();

        // Command line arguments parsing & basic processing
        MinalacGeneratorCLI cli = new MinalacGeneratorCLI();
        cli.parse(args);

        // Generation parsing
        ParamsParser parser = new ParamsParser();
        // TODO: Static method that provides a ParamsParser with all default renderers
        // If those name values are modified, update the documentation accordingly
        parser.registerRenderer("vector", VectorRendererParams.class);
        parser.registerRenderer("heightmap", HeightmapRendererParams.class);
        parser.registerRenderer("nature", NatureRendererParams.class);
        Generation generation = parser.parse(cli.readParameters()).create();

        System.out.println("Creation of the map.");

        String crsName = "EPSG:2154";
        CoordinateReferenceSystem crs = CRS.decode(crsName);
        Envelope envelope = generation.getEnvelopeForCRS(crs);
        String bboxURL = "BBOX=" + envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY();

        Scheduler scheduler = new Scheduler();

        // Downloads

        scheduler.schedule("models.ground", () -> {
            log("Downloading height map");
            MapToWorldConverter converter;
            try {
                converter = generation.makeCoordsConverter(crs);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            }
            retrieveDataAndFillModelStore(generation.models(), "mnt",
                new WMSFloatBilDataProvider("https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS", crs, envelope),
                new FloatMatrixProcessor(converter)
            );
            log("Downloaded heightmap");
        });

        scheduler.schedule("models.buildings", () -> {
            log("Downloading buildings");
            MapToWorldConverter converter; // This is supposed to be the layer CRS (actually the same for this demo)
            try {
                converter = generation.makeCoordsConverter(crs);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            }
            retrieveDataAndFillModelStore(generation.models(), "building",
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

        // TODO: un bete copier coller, a voir si je peux pas simplifier ce gros code avec le code d'au dessus
        scheduler.schedule("models.natures", () -> {
            log("Downloading natures");
            MapToWorldConverter converter; // This is supposed to be the layer CRS (actually the same for this demo)
            try {
                converter = generation.makeCoordsConverter(crs);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            }
            retrieveDataAndFillModelStore(generation.models(), "natures",
                new WFS1_1_GML3_1_DataProvider("https://data.geopf.fr/wfs/wfs?SERVICE=WFS&REQUEST=GetFeature&VERSION=2.0.0&TYPENAMES=BDTOPO_V3:zone_de_vegetation&STARTINDEX=0&COUNT=1000&SRSNAME=urn:ogc:def:crs:EPSG::2154&" + bboxURL + ",urn:ogc:def:crs:EPSG::2154&outputFormat=text%2Fxml%3B%20subtype%3Dgml%2F3.1.1"),
                new GeoToolsVectorProcessor(converter),
                new MetadataCopyPostProcessor("nature", "nature", false, false),
                new MetadataParsePostProcessor<>(
                    "nature",
                    String.class,
                    obj -> {
                        if (obj instanceof String string)
                            return string;
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
                        // TODO: voir ce que je peux faire s'il y a pas de metadonnée nature (probablement rien)
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
                        Object nature = model.getMetadata("nature");
                        if (!(nature instanceof String))
                            throw new GenerationFailedException("Illegal nature value: " + (nature == null ? "null" : (nature + " (" + nature.getClass() + ")")));
                        return model;
                    }
                }
            );
            log("Downloaded natures");
        });

        // Rendering

        SimpleVoxelPattern soilPattern = new SimpleVoxelPattern();
        soilPattern.set(0, 0, 0, generation.world().getFactory().createVoxelType(SemanticType.GRASS));
        soilPattern.set(new WorldBBox3d(0, 0, -3, 1, 1, 3), generation.world().getFactory().createVoxelType(SemanticType.DIRT));
        soilPattern.set(new WorldBBox3d(0, 0, -23, 1, 1, 20), generation.world().getFactory().createVoxelType(SemanticType.STONE));

        scheduler.schedule("renderers.heightmap", () -> {
            log("Filling altitude heightmap");
            generation.renderers().get("heightmap-ground").render(generation.world().limits());
            log("Altitude heightmap filled");
        }, "models.ground");

        // TODO: Create GroundRendererParams when VoxelPattern deserialization is implemented
        scheduler.schedule("renderers.ground", () -> {
            log("Rendering ground");
            new GroundRenderer(
                generation.heightmaps().get("ground"),
                soilPattern
            ).render(generation.world().limits());
            log("Ground rendered");
        }, "renderers.heightmap");

        scheduler.schedule("renderers.buildings", () -> {
            log("Placing buildings");
            generation.renderers().get("building").render(generation.world().limits());
            log("Placed buildings");
        }, "renderers.heightmap", "models.buildings");

        scheduler.schedule("renderers.natures", () -> {
            log("Placing natures");
            generation.renderers().get("nature").render(generation.world().limits());
            log("Placed natures");
        }, "renderers.heightmap", "models.natures", "renderers.ground");

        // Get work done!

        scheduler.start();
        try {
            scheduler.waitUntilAllTasksFinished(5, TimeUnit.MINUTES);
        } finally {
            scheduler.shutdown();
        }

        System.out.println("Saving world");
        VoxelWorldMetadata metadata = generation.world().getMetadata();
        metadata.setSpawn(new WorldCoords3d(0, 0, generation.heightmaps().get("ground").get(0, 0) + 1));
        metadata.setWorldName("Minalac");
        save(cli.getOutputPath().toFile(), generation.world());

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

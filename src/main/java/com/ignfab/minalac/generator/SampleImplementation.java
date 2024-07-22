package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.parsing.ParamsParser;
import com.ignfab.minalac.generator.parsing.ParseException;
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
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.NoSuchElementException;
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
            log("heightmaps.ground", "Downloading heightmap");
            try {
                fillGroundHeightmap("https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES=&CRS=" + crsName + "&" + bboxURL, groundHeightmap, generation.getVerticalScale());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            log("heightmaps.ground", "Downloaded heightmap");
        });

        scheduler.schedule("models.buildings", () -> {
            log("models.buildings", "Downloading buildings");
            try {
                downloadVectorFeatures(
                    new WFS1_1_GML3_1_DataProvider("https://data.geopf.fr/wfs/wfs?SERVICE=WFS&REQUEST=GetFeature&VERSION=2.0.0&TYPENAMES=BDTOPO_V3:batiment&STARTINDEX=0&COUNT=1000&SRSNAME=urn:ogc:def:crs:EPSG::2154&" + bboxURL + ",urn:ogc:def:crs:EPSG::2154&outputFormat=text%2Fxml%3B%20subtype%3Dgml%2F3.1.1"),
                    generation.makeCoordsConverter(crs),  // This is supposed to be the layer CRS (actually the same for this demo)
                    "building",
                    store);
            } catch (TransformException | IOException | ParserConfigurationException | SAXException | FactoryException e) {
                throw new RuntimeException(e);
            }
            log("models.buildings", "Downloaded buildings");
        });

        // Rendering
        scheduler.schedule("renderers.ground", () -> {
            log("renderers.ground", "Placing ground");
            placeVoxelFromHeightmap(groundHeightmap, world);
            log("renderers.ground", "Placed ground");
        }, "heightmaps.ground");

        scheduler.schedule("renderers.buildings", () -> {
            log("renderers.buildings", "Placing buildings");
            new VectorRenderer(
                groundHeightmap,
                store.getByType("building"),
                world.getFactory().createVoxelType(SemanticType.COBBLE),
                world.getFactory().createVoxelType(SemanticType.BRICK)
            ).render(generation.getWorldBBox2d().to3d(-32_000, 64_000));
            log("renderers.buildings", "Placed buildings");
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

    private static void log(String prefix, String message) {
        System.out.printf("[%s] (%s) %s%n", Thread.currentThread().getName(), prefix, message);
    }

    private static void downloadVectorFeatures(WFS1_1_GML3_1_DataProvider provider, CoordsConverter converter, String type, ModelStore store)
            throws NoSuchElementException, TransformException, IOException, ParserConfigurationException, SAXException {
        try (SimpleFeatureIterator iterator = provider.getFeatures().features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                JTSGeometryModel model = new JTSGeometryModel((Geometry) feature.getDefaultGeometry(), converter);
                Object height = feature.getAttribute("hauteur");

                // TODO: Value 20 is a temporary value for building renderings.
                model.setMetadata("height", 20.0);
                if (height instanceof String s) {
                    try {
                        model.setMetadata("height", Double.valueOf(s));
                    } catch (NumberFormatException e) {}
                } else if (height instanceof Number n) {
                    model.setMetadata("height", n);
                }
                store.add(type, model);
            }
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

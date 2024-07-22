package com.ignfab.minalac.generator;


import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.inputs.WFS1_1_GML3_1_DataProvider;
import com.ignfab.minalac.generator.models.GeometryModel;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.renderers.VectorRenderer;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * This is a temporary class to have an idea of how the program works.
 * It generates a Minetest map which is a 3D rendering from a heightmap
 */
public final class SampleImplementation {
    private SampleImplementation() {
        throw new UnsupportedOperationException();
    }

    private static final Map<String, Supplier<VoxelWorld>> FORMATS = Map.of(
        "minecraft", MCVoxelWorld::new,
        "minetest", MTVoxelWorld::new
    );

    public static void main(String[] args)
            throws IOException, OutOfWorldException, MapWriteException, SAXException, FactoryException,
            ParserConfigurationException, TransformException {
        long start = System.currentTimeMillis();
        HttpTrustAllSSL.applyGlobally();
        if (args.length != 9) {
            System.out.println("There must be nine arguments : directoryPath, crs, centerX, centerY, extendX, extendY, horizontalScale, verticalScale, format");
        } else {
            //Example : "/home/john/.minetest/worlds/map/"
            String directoryPath = args[0];

            //Example : "EPSG:2154"
            String crsName = args[1];
            CoordinateReferenceSystem crs = CRS.decode(crsName);

            // Center coordinates (in CRS), example : 600000 6340000
            // TODO: Center should be in Lon/lat in WGS84
            float centerX = Float.parseFloat(args[2]);
            float centerY = Float.parseFloat(args[3]);

            // Extends in voxel, example: 1000 1000, must be >0
            int extendX = Integer.parseInt(args[4]);
            int extendY = Integer.parseInt(args[5]);

            // Horizontal scale (horizontal size of voxel in meters), example: 1.0, must be >0
            float horizontalScale = Float.parseFloat(args[6]);

            // Vertical scale (vertical size of voxel in meters), example: 10.0, must be >0
            float verticalScale = Float.parseFloat(args[7]);

            // Output format
            String format = args[8].toLowerCase();
            if (!FORMATS.containsKey(format))
                throw new IllegalArgumentException("Unknown format: " + format);

            System.out.println("Creation of the map.");

            Generation generation = new Generation(
                crs,
                centerX, centerY,
                extendX, extendY,
                horizontalScale, verticalScale
            );

            Envelope envelope = generation.getEnvelopeForCRS(crs);
            String bboxURL = "BBOX=" + envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY();

            // Downloads
            ModelStore store = new ModelStore();

            System.out.println("Downloading height map");
            Heightmap heightMap = createGroundHeightMap("https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES=&CRS=" + crsName + "&" + bboxURL, extendX, extendY, verticalScale);

            System.out.println("Downloading buildings");
            downloadVectorFeatures(
                new WFS1_1_GML3_1_DataProvider("https://data.geopf.fr/wfs/wfs?SERVICE=WFS&REQUEST=GetFeature&VERSION=2.0.0&TYPENAMES=BDTOPO_V3:batiment&STARTINDEX=0&COUNT=1000&SRSNAME=urn:ogc:def:crs:EPSG::2154&" + bboxURL + ",urn:ogc:def:crs:EPSG::2154&outputFormat=text%2Fxml%3B%20subtype%3Dgml%2F3.1.1"),
                generation.makeCoordsConverter(crs),  // This is supposed to be the layer CRS (actually the same for this demo)
                "building",
                store);

            System.out.println("World creation");
            VoxelWorld world = FORMATS.get(format).get();

            System.out.println("Placing ground");
            placeVoxelFromHeightMap(heightMap, world);

            System.out.println("Placing buildings");
            new VectorRenderer(
                heightMap,
                store.getByType("building"),
                world.getFactory().createVoxelType(SemanticType.COBBLE),
                world.getFactory().createVoxelType(SemanticType.BRICK)
            ).render(generation.getWorldBBox2d().to3d(-32_000, 64_000));

            System.out.println("Saving");
            VoxelWorldMetadata metadata = world.getMetadata();
            metadata.setSpawn(new WorldCoords3d(0, 0, heightMap.get(0, 0) + 1));
            metadata.setWorldName("Minalac");
            metadata.setBbox(new WorldBBox3d(-extendX / 2, -extendY / 2, 0, extendX, extendY, 1)); // Z is ignored for now
            save(new File(directoryPath), world);

            System.out.println("Done");

            // TODO: That's not working. It's an attempt to avoid InterruptExecption thrown by threads started by CRS.decode
            CRS.cleanupThreadLocals();
        }
        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start) / 1000 + "s");
    }

    private static void downloadVectorFeatures(WFS1_1_GML3_1_DataProvider provider, CoordsConverter converter, String type, ModelStore store)
            throws NoSuchElementException, TransformException, IOException, ParserConfigurationException, SAXException {
        SimpleFeatureIterator iterator = provider.getFeatures().features();
        while (iterator.hasNext())
            store.add(type, new GeometryModel((Geometry) iterator.next().getDefaultGeometry(), converter));
    }

    private static void placeVoxelFromHeightMap(Heightmap map, VoxelWorld world) throws OutOfWorldException {
        VoxelType grassVT = world.getFactory().createVoxelType(SemanticType.GRASS);
        VoxelType stoneVT = world.getFactory().createVoxelType(SemanticType.STONE);
        VoxelType dirtVT = world.getFactory().createVoxelType(SemanticType.DIRT);

        for (Voxel3d voxel : map.voxelize3d(map.bbox().to3d(-32_000, 64_000))) {
            WorldCoords3d coords = voxel.coords();
            int x = coords.x();
            int y = coords.y();
            int z = coords.z();
            grassVT.place(x, y, z);
            dirtVT.place(x, y, (z - 1));
            dirtVT.place(x, y, (z - 2));

            for (int zStone = z - 3; zStone > z - (3 + 10); zStone--) {
                stoneVT.place(x, y, zStone);
            }
        }
    }

    private static Heightmap createGroundHeightMap(String partialUrl, int width, int height, float verticalScale) throws MalformedURLException {
        float[] mntArray;
        byte[] data;
        URL url = new URL(partialUrl + "&WIDTH=" + width + "&HEIGHT=" + height);

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

        mntArray = byteArrayToFloatArray(data);

        int xMin = -width / 2;
        int yMin = -height / 2;

        Heightmap heightMap = new Heightmap(xMin, yMin, width, height, 0);

        int index = 0;

        for (int y = height - 1; y >= 0; y--) // In raster, Y axis is downwards
            for (int x = 0; x < width; x++) {
                heightMap.set(x + xMin, y + yMin, (int) (mntArray[index] / verticalScale));
                index++;
            }

        return heightMap;
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

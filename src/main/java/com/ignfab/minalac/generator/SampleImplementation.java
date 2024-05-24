package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.WFS2DataProvider;
import com.ignfab.minalac.generator.models.BufferedImageChunk;
import com.ignfab.minalac.generator.models.GeometryModel;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.ArrayChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIterator;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIteratorAll;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.data.simple.SimpleFeatureCollection;
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
import java.util.function.Supplier;

/**
 * This is a temporary class to have an idea of how the program works.
 * It generates a Minetest map which is a 3D rendering from a heightmap
 */
public class SampleImplementation {
    private static final Map<String, Supplier<VoxelWorld>> FORMATS = Map.of(
        "minecraft", MCVoxelWorld::new,
        "minetest", MTVoxelWorld::new
    );

    public static void main(String[] args)
    throws IOException, OutOfWorldException, MapWriteException, SAXException,
           NoSuchAuthorityCodeException, FactoryException,
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

            // Creation of heightmap

            System.out.println("Downloading height map");
            HeightMap heightMap = createGroundHeightMap("https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES=&CRS=" + crsName + "&" + bboxURL, extendX, extendY, verticalScale);

            System.out.println("World creation");
            VoxelWorld world = FORMATS.get(format).get();
            System.out.println("Placing ground");
            placeVoxelFromHeightMap(heightMap, world);

            // Add a vector layer
            System.out.println("Add vector layer");
            WFS2DataProvider provider = new WFS2DataProvider("https://data.geopf.fr/wfs/wfs?SERVICE=WFS&REQUEST=GetFeature&VERSION=2.0.0&TYPENAMES=BDTOPO_V3:batiment&STARTINDEX=0&COUNT=1000&SRSNAME=urn:ogc:def:crs:EPSG::2154&" + bboxURL +",urn:ogc:def:crs:EPSG::2154");
            placeVectorFeatures(provider.getFeatures(),
                generation.makeCoordsConverter(crs), // This is supposed to be the layer CRS (actually the same for this demo)
                world, heightMap);

            System.out.println("Saving");
            VoxelWorldMetadata metadata = world.getMetadata();
            metadata.setSpawn(0, 0, remap(heightMap.get(0, 0) + 1, heightMap.getMin(), heightMap.getMax(), 64, 255)); // Tmp for Minecraft
            metadata.setWorldName("Minalac");
            metadata.setBbox(-extendX / 2, -extendY / 2, 0, extendX / 2, extendY / 2, 0); // Z is ignored for now
            save(new File(directoryPath), world);

            System.out.println("Done");

            // TODO: That's not working. It's an attempt to avoid InterruptExecption thrown by threads started by CRS.decode
            CRS.cleanupThreadLocals();
        }
        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start) / 1000 + "s");
    }

    private static void placeVectorFeatures(SimpleFeatureCollection features, CoordsConverter converter, VoxelWorld world, HeightMap heightMap)
        throws TransformException, OutOfWorldException {
        VoxelType insideVT = world.getFactory().createVoxelType(SemanticType.COBBLE);
        VoxelType wallVT = world.getFactory().createVoxelType(SemanticType.BRICK);
        try (
            SimpleFeatureIterator iterator = features.features();
        ) {
            while( iterator.hasNext() ){
                SimpleFeature feature = iterator.next();

                // Create a model out of feature geometry
                GeometryModel model = new GeometryModel((Geometry)feature.getDefaultGeometry(), converter);

                // Rasterize that model into a chunk
                BufferedImageChunk chunk = model.getChunk();

                // Iterate over chunk and draw shape on map at heightMap altitude
                for (Chunk2dElement element : chunk) {
                    WorldCoords2d c = element.getCoords();
                    // TODO: Make Iterator able to intersect with another box
                    // TODO: Have a world bbox rather
                    if (heightMap.bbox().contains(c))
                        switch(element.getValue()) {
                            case GeometryModel.INSIDE:
                                insideVT.place(c.getX(), c.getY(), heightMap.get(c) + 1);
                                break;
                            case GeometryModel.BORDER:
                                wallVT.place(c.getX(), c.getY(), heightMap.get(c) + 1);
                                break;
                        }
                }
            }
        }
    }

    private static void placeVoxelFromHeightMap(HeightMap map, VoxelWorld world) throws OutOfWorldException {
        VoxelType grassVT = world.getFactory().createVoxelType(SemanticType.GRASS);
        VoxelType stoneVT = world.getFactory().createVoxelType(SemanticType.STONE);
        VoxelType dirtVT = world.getFactory().createVoxelType(SemanticType.DIRT);

        for (Chunk2dElement element : map) {
            int x = element.getX();
            int y = element.getY();
            int z = remap(element.getValue(), map.getMin(), map.getMax(), 64, 255); // Temporary workaround to generate Minecraft worlds
            grassVT.place(x, y, z);
            dirtVT.place(x, y, (z - 1));
            dirtVT.place(x, y, (z - 2));

            for (int z_stone = z - 3; z_stone > z - (3 + 10); z_stone--) {
                stoneVT.place(x, y, z_stone);
            }
        }
    }

    private static HeightMap createGroundHeightMap(String partialUrl, int width, int height, float verticalScale) throws MalformedURLException {
        float[] mntArray;
        byte[] data;
        URL url = new URL(partialUrl + "&WIDTH=" + width + "&HEIGHT=" + height);

        try (InputStream inputStream = url.openStream()) {
            int total, read;
            total = 0;
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

        HeightMap heightMap = new HeightMap(xMin, yMin, width, height, 0);

        int index = 0;

        for (int y = height - 1 ; y >= 0 ; y--) // In raster, Y axis is downwards
            for (int x = 0 ; x < width ; x++) {
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

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents)
                deleteDirectory(file);
        }
        return directoryToBeDeleted.delete();
    }

    private static int remap(int value, int fromMin, int fromMax, int toMin, int toMax) {
        return (int) ((value - fromMin) / (double) (fromMax - fromMin) * (toMax - toMin) + toMin);
    }

    //This class will probably be added on an upcoming pull-request (since it doesn't belong to the package utils.world2d.chunk)
    private static class HeightMap extends ArrayChunk2d implements IterableChunk2d {
        private int min, max;

        public HeightMap(int originX, int originY, int sizeX, int sizeY, int defaultValue) {
            super(originX, originY, sizeX, sizeY, defaultValue);
            min = max = defaultValue;
        }

        public HeightMap(WorldBBox2d box, int defaultValue) {
            super(box, defaultValue);
            min = max = defaultValue;
        }

        public HeightMap(WorldCoords2d coords, WorldSize2d size, int defaultValue) {
            super(coords, size, defaultValue);
            min = max = defaultValue;
        }

        @Override
        public void set(int x, int y, int value) {
            super.set(x, y, value);
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        @Override
        public void set(WorldCoords2d coords, int value) {
            super.set(coords, value);
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        @Override
        public Chunk2dIterator iterator() {
            return new Chunk2dIteratorAll(this);
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }
}

package com.ignfab.minalac.generator;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ignfab.minalac.generator.world.*;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.utils.world2d.*;
import com.ignfab.minalac.generator.utils.world2d.chunk.*;
import com.ignfab.minalac.generator.utils.world2d.iterator.*;

/**
 * This is a temporary class to have an idea of how the program works.
 * It generates a Minetest map which is a 3D rendering from a heightmap
 */
public class SampleImplementation {
    public static void main(String[] args) throws IOException, OutOfWorldException, MapWriteException {
        if (args.length != 5) {
            System.out.println("There must be five arguments : directoryPath, baseURL, width, height, verticalScale");
        } else {
            //Example : "/home/john/.minetest/worlds/map/"
            String directoryPath = args[0];

            //Example : https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES=&CRS=EPSG:2154&BBOX=595000,6335000,605000,6345000
            String partialURL = args[1];

            //Example : 1000
            int width = Integer.parseInt(args[2]);

            //Example : 1000
            int height = Integer.parseInt(args[3]);

            //Example : 10 (in meter per voxel)
            float verticalScale = Float.parseFloat(args[4]);

            System.out.println("Creation of the map ..." +
                    "\ndirectoryPath: " + directoryPath +
                    "\npartialURL: " + partialURL +
                    "\nwidth: " + width +
                    "\nheight: " + height +
                    "\nverticalScale: " + verticalScale);

            HeightMap heightMap = createGroundHeightMap(partialURL, width, height, verticalScale);

            VoxelWorld world = new MTVoxelWorld();
            placeVoxelFromHeightMap(heightMap, world);
            save(directoryPath, world);

            setStaticSpawnPoint(directoryPath, 0, heightMap.get(0, 0) + 1, 0);
        }
    }

    private static void placeVoxelFromHeightMap(HeightMap map, VoxelWorld world) throws OutOfWorldException {
        VoxelType grassVT = world.getFactory().createVoxelType(SemanticType.Grass);
        VoxelType stoneVT = world.getFactory().createVoxelType(SemanticType.Stone);
        VoxelType dirtVT = world.getFactory().createVoxelType(SemanticType.Dirt);

        for (Chunk2dElement element : map) {
            int x = element.getX();
            int y = element.getY();
            int z = element.getValue();
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

        int xMinHeightMap = -width / 2, yMinHeightMap = -height / 2;
        HeightMap heightMap = new HeightMap(xMinHeightMap, yMinHeightMap, width, height, 0);

        int x_arr, y_arr, x_world, y_world;
        for (int i = 0; i < mntArray.length; i++) {
            x_arr = i % width;
            y_arr = i / width;
            x_world = x_arr + xMinHeightMap;
            y_world = -(y_arr + yMinHeightMap) - 1; //-1 so min y_world matches yMinHeightMap (There is an offset due to y-axis inversion),
            heightMap.set(x_world, y_world, (int) (mntArray[i] / verticalScale));
        }
        return heightMap;
    }

    private static float[] byteArrayToFloatArray(byte[] byteData) {
        float[] floatData = new float[byteData.length / 4];
        ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floatData);
        return floatData;
    }

    private static void save(String directory, VoxelWorld world) throws MapWriteException {
        deleteDirectory(new File(directory));
        world.save(directory);
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents)
                deleteDirectory(file);
        }
        return directoryToBeDeleted.delete();
    }

    private static void setStaticSpawnPoint(String directoryFullPath, int x, int y, int z) throws IOException {
        directoryFullPath = directoryFullPath.endsWith("/") ? directoryFullPath : directoryFullPath + "/";
        System.out.println(directoryFullPath);
        File dir = new File(directoryFullPath + "worldmods/ign_spawn/");
        if (dir.mkdirs()) {
            File luaScript = new File(dir.getAbsolutePath() + "/init.lua");
            luaScript.createNewFile();

            FileWriter fileWriter = new FileWriter(luaScript);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("minetest.setting_set(\"static_spawnpoint\", \"" + x + ", " + y + ", " + z + "\")");
            printWriter.close();
        }
    }

    //This class will probably be added on an upcoming pull-request (since it doesn't belong to the package utils.world2d.chunk)
    private static class HeightMap extends ArrayChunk2d implements IterableChunk2d {
        public HeightMap(int originX, int originY, int sizeX, int sizeY, int defaultValue) {
            super(originX, originY, sizeX, sizeY, defaultValue);
        }

        public HeightMap(WorldBBox2d box, int defaultValue) {
            super(box, defaultValue);
        }

        public HeightMap(WorldCoords2d coords, WorldSize2d size, int defaultValue) {
            super(coords, size, defaultValue);
        }

        @Override
        public Chunk2dIterator iterator() {
            return new Chunk2dIteratorAll(this);
        }
    }
}
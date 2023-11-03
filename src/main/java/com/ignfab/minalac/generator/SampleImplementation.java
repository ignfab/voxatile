package com.ignfab.minalac.generator;

import com.ignfab.minalac.generator.minetest.MTVoxelWorld;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * This is a temporary class to have an idea of how the program works.
 * It generates a Minetest map which is a 3D rendering from a heightmap
 */
public class SampleImplementation {
    public static void main(String[] args) throws IOException, OutOfWorldException, MapWriteException {
        if (args.length != 2) {
            System.out.println("There must be two arguments : directoryPath and url");
        } else {
            System.out.println("Creation of the map ...");
            System.out.println(args[0]);
            System.out.println(args[1]);

            //Parent directory must exist
            //Example : "/home/john/.minetest/worlds/map/"
            String directoryFullPath = args[0];

            //The function createWorldFromUrl doesn't, for now, handle the parameters.
            //BBOX has to be a square.
            //Width and height have to be one-tenth of the side of the side length of the BBOX's square

            //Example "https://data.geopf.fr/wms-r/wms?LAYERS=RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS&FORMAT=image/x-bil;bits=32&SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&STYLES=&CRS=EPSG:2154&BBOX=595000,6335000,605000,6345000&WIDTH=1000&HEIGHT=1000"
            String dataUrl = args[1];

            float[] mntArray = getHeightMap(dataUrl);

            VoxelWorld worldMnt = new MTVoxelWorld();
            createWorldFromMnt(mntArray, worldMnt);
            worldMnt.save(directoryFullPath);

            int midPoint = (int) (mntArray.length + Math.sqrt(mntArray.length)) / 2;
            setStaticSpawnPoint(directoryFullPath, 0, (int) mntArray[midPoint] / 10 + 1, 0);
        }
    }

    private static void createWorldFromMnt(float[] mntArray, VoxelWorld world) throws OutOfWorldException {
        int worldLength = (int) Math.sqrt(mntArray.length);

        int x, y, z;

        VoxelType grassVT = world.getFactory().createVoxelType(SemanticType.Grass);
        VoxelType stoneVT = world.getFactory().createVoxelType(SemanticType.Stone);
        VoxelType dirtVT = world.getFactory().createVoxelType(SemanticType.Dirt);

        for (int i = 0; i < mntArray.length; i++) {
            //Temporary translation so the player spawn at the center of the generated map (player info isn't generated yet on this version)
            x = i % worldLength - worldLength / 2;
            //Ratio between the side length of the BBOX and width/heigth length
            //In this example, we assume that the ratio given by the URL is always 10
            y = (int) mntArray[i] / 10;
            z = i / worldLength - worldLength / 2;

            grassVT.place(x, y, z);
            dirtVT.place(x, (y - 1), z);
            dirtVT.place(x, (y - 2), z);

            for (int y_stone = y - 3; y_stone > y - (3 + 10); y_stone--) {
                stoneVT.place(x, y_stone, z);
            }
        }
    }

    private static float[] getHeightMap(String urlString) throws MalformedURLException {
        float[] mntArray;
        URL url = new URL(urlString);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = url.openStream()) {
            int n;
            byte[] buffer = new byte[1];
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] byteArray = outputStream.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN);

        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        mntArray = new float[floatBuffer.capacity()];
        floatBuffer.get(mntArray);

        return mntArray;
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
}
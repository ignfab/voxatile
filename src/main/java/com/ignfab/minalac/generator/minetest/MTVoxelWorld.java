package com.ignfab.minalac.generator.minetest;

import com.ignfab.minalac.generator.MapWriteException;
import com.ignfab.minalac.generator.OutOfWorldException;
import com.ignfab.minalac.generator.VoxelTypeFactory;
import com.ignfab.minalac.generator.VoxelWorld;
import com.ignfab.minalac.generator.minetest.utils.SqlLiteMapWriter;
import com.ignfab.minalac.generator.minetest.voxelType.MTVoxelType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MTVoxelWorld implements VoxelWorld {
    private VoxelTypeFactory factory;
    private HashMap<Integer, Block> blocks;
    //See MAX_MAP_GENERATION_LIMIT constant on Minetest
    //https://github.com/minetest/minetest/blob/e10d8080ba6e53e0f3c4b20b32304f8bb36e5958/src/constants.h#L70
    private static final int limitPosition = 31007;

    public MTVoxelWorld() {
        this.factory = new MTVoxelTypeFactory(this);
        this.blocks = new HashMap<>();
    }

    @Override
    public VoxelTypeFactory getFactory() {
        return this.factory;
    }

    public void set(int x, int y, int z, MTVoxelType voxel) throws OutOfWorldException {
        if (-limitPosition > x || x > limitPosition ||
                -limitPosition > y || y > limitPosition ||
                -limitPosition > z || z > limitPosition
        )
            throw new OutOfWorldException();

        int pos = getPosValue(getBlockPosition(x), getBlockPosition(y), getBlockPosition(z));
        Block block = blocks.get(pos);

        if (block == null) {
            block = new Block();
        }
        block.set(getNodeRelativePosition(x), getNodeRelativePosition(y), getNodeRelativePosition(z), voxel);
        this.blocks.put(pos, block);
    }

    public void save(String directoryFullPath) throws MapWriteException {
        File mapDirectory = new File(directoryFullPath);
        if (mapDirectory.mkdir()) {
            //Files containing the necessary information to identify the folder as a map
            createFile(mapDirectory.getAbsolutePath() + "/world.mt", "enable_damage = true\n" +
                    "creative_mode = true\n" +
                    "auth_backend = sqlite3\n" +
                    "player_backend = sqlite3\n" +
                    "gameid = minetest");
            createFile(mapDirectory.getAbsolutePath() + "/map_meta.txt", "mapgen_limit = 31000\n" +
                    "mg_name = singlenode\n" +
                    "[end_of_params]");

            SqlLiteMapWriter database = new SqlLiteMapWriter(mapDirectory.getAbsolutePath() + "/");
            for (Map.Entry<Integer, Block> entry : blocks.entrySet()) {
                database.insertBlock(entry.getKey(), entry.getValue());
            }
        } else
            throw new MapWriteException("Can not generate the map since the folder" + mapDirectory.getAbsolutePath() + " can not be created");
    }

    private void createFile(String path, String content) throws MapWriteException {
        File file = new File(path);
        FileWriter fileWriter;
        try {
            file.createNewFile();
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            throw new MapWriteException(e.getMessage());
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(content);
        printWriter.close();
    }

    //See position hashing algorithm on world format documentation
    //https://github.com/minetest/minetest/blob/master/doc/world_format.md#position-hashing
    private int getPosValue(int x, int y, int z) {
        return z * 16777216 + y * 4096 + x;
    }

    private int getNodeRelativePosition(int p) {
        p = p % 16;
        return p < 0 ? p + 16 : p;
    }

    //See getContainerPos(s16 p, s16 d)
    //https://github.com/minetest/minetest/blob/master/src/util/numeric.h#L45
    private int getBlockPosition(int p) {
        return (p >= 0 ? p : p - 16 + 1) / 16;
    }
}
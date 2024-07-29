package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.utils.SQLiteMapWriter;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link VoxelWorld} that creates a playable world specifically for Minetest.
 */
public class MTVoxelWorld implements VoxelWorld {
    private final VoxelTypeFactory factory;
    private final VoxelWorldMetadata metadata;
    private final HashMap<Integer, Block> blocks;
    //See MAX_MAP_GENERATION_LIMIT constant on Minetest
    //https://github.com/minetest/minetest/blob/master/src/constants.h#L69
    private static final int LIMIT_POSITION = 31_007;

    /**
     * Constructs a new {@code MTVoxelWorld}.
     */
    public MTVoxelWorld() {
        this.factory = new MTVoxelTypeFactory(this);
        metadata = new VoxelWorldMetadata();
        this.blocks = new HashMap<>();
    }

    /**
     * Returns the factory for creating {@link MTVoxelType}.
     *
     * @return the factory for creating voxels
     */
    @Override
    public VoxelTypeFactory getFactory() {
        return this.factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoxelWorldMetadata getMetadata() {
        return metadata;
    }

    /**
     * Places the voxel into this world at the specified coordinates.
     * The specified coordinates must be in the coordinate system used by Minetest.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     * @param voxel the voxel to place
     * @throws OutOfWorldException if the given coordinates are outside the world limits
     */
    protected void set(int x, int y, int z, MTVoxelType voxel) throws OutOfWorldException {
        if (-LIMIT_POSITION > x || x > LIMIT_POSITION
                || -LIMIT_POSITION > y || y > LIMIT_POSITION
                || -LIMIT_POSITION > z || z > LIMIT_POSITION)
            throw new OutOfWorldException();

        int pos = getPosValue(getBlockPosition(x), getBlockPosition(y), getBlockPosition(z));
        Block block = blocks.get(pos);

        if (block == null) {
            block = new Block();
        }
        block.set(getNodeRelativePosition(x), getNodeRelativePosition(y), getNodeRelativePosition(z), voxel);
        this.blocks.put(pos, block);
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minetest.
     */
    @Override
    public void save(File destination) throws MapWriteException {
        createFile(new File(destination, "world.mt"), """
                world_name = %s
                enable_damage = true
                creative_mode = true
                auth_backend = sqlite3
                player_backend = sqlite3
                gameid = minetest""".formatted(metadata.getWorldName()));
        createFile(new File(destination, "map_meta.txt"), """
                mapgen_limit = 31000
                mg_name = singlenode
                [end_of_params]""");
        createFile(new File(destination, "worldmods/ign_spawn/init.lua"), """
                minetest.setting_set("static_spawnpoint", "%d, %d, %d")""".formatted(metadata.getSpawn().x(), metadata.getSpawn().z(), metadata.getSpawn().y())); // XYZ => XZY

        SQLiteMapWriter database = new SQLiteMapWriter(destination);
        for (Map.Entry<Integer, Block> entry : blocks.entrySet()) {
            database.insertBlock(entry.getKey(), entry.getValue());
        }
    }

    private void createFile(File file, String content) throws MapWriteException {
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new MapWriteException(e);
        }
        try (
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter)
        ) {
            printWriter.println(content);
        } catch (IOException e) {
            throw new MapWriteException(e);
        }
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

package com.ignfab.minalac.generator.outputs.minetest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.outputs.minetest.utils.SQLiteMapWriter;
import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

/**
 * Implementation of {@link VoxelWorld} that creates a playable world specifically for Minetest.
 */
public class MTVoxelWorld extends VoxelWorld {
    private final HashMap<Integer, Block> blocks;
    // See MAX_MAP_GENERATION_LIMIT constant on Minetest
    // https://github.com/minetest/minetest/blob/master/src/constants.h#L69
    private static final WorldBBox3d MAX_LIMIT = new WorldBBox3d(
        new WorldCoords3d(-31_007, -31_007, -31_007),
        new WorldCoords3d(31_007, 31_007, 31_007)
    );

    /**
     * Constructs a new {@code MTVoxelWorld}.
     * The limits of the world have to be set using {@link #setLimits(WorldBBox3d)}
     */
    public MTVoxelWorld() {
        super(new VoxelWorldMetadata());
        blocks = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     * Maximum limits represent the hard limits of the format.
     */
    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMIT;
    }

    // Retrieves or creates the mapblock corresponding to given voxel position.
    private Block getOrCreateBlock(int blockX, int blockY, int blockZ) {
        Integer pos = coordsToPos(blockX, blockY, blockZ);

        Block block = blocks.get(pos);
        if (block == null)
            synchronized (blocks) {
                block = blocks.get(pos);
                if (block == null) {
                    block = new Block();
                    blocks.put(pos, block);
                }
            }
        return block;
    }

    private Block getBlock(int blockX, int blockY, int blockZ) {
        return blocks.get(coordsToPos(blockX, blockY, blockZ));
    }

    private Integer coordsToPos(int blockX, int blockY, int blockZ) {
        // See position hashing algorithm on world format documentation
        // https://github.com/minetest/minetest/blob/master/doc/world_format.md#position-hashing
        return blockZ * 16777216 + blockY * 4096 + blockX;
    }

    /**
     * Places the voxel into this world at the specified coordinates.
     * The specified coordinates must be in the coordinate system used by Minetest.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     * @param voxel the voxel to place
     */
    protected void set(int x, int y, int z, MTVoxelType voxel) {
        // (In-Game coords to world coords) XZY => XYZ
        if (!limits().contains(x, z, y)) return;

        getOrCreateBlock(x >> 4, y >> 4, z >> 4).set(x & 0x0f, y & 0x0f, z & 0x0f, voxel);

        // (In-Game coords to world coords) XZY => XYZ
        updateHeightmaps(x, z, y);
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

    /**
     * {@inheritDoc}
     * The returned voxel is not necessarily one placed using {@link VoxelType#place}.
     * It may be an air node created when a {@link Block} is initialized.
     */
    @Override
    public VoxelType getVoxel(int x, int y, int z) {
        // X/Y/Z => X/Z/Y
        Block block = getBlock(x >> 4, z >> 4, y >> 4);

        if (block == null) return null;

        // X/Y/Z => X/Z/Y
        return block.get(x & 0x0f, z & 0x0f, y & 0x0f, this);
    }
}

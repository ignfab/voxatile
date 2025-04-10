package com.ignfab.minalac.generator.outputs.minetest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.outputs.minetest.utils.SQLiteMapWriter;
import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * Implementation of {@link VoxelWorldTile} for Minetest.
 */
public class MTVoxelWorldTile extends VoxelWorldTile {
    private final HashMap<Integer, Block> blocks = new HashMap<>();

    /**
     * Creates a new {@code MTVoxelWorldTile}.
     *
     * @param world {@link MTVoxelWorld} of which this tile is a part
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public MTVoxelWorldTile(MTVoxelWorld world, WorldBBox3d limits) {
        super(world, limits);
    }

    // Retrieves or creates the mapblock corresponding to given voxel position.
    private Block getOrCreateBlock(int x, int y, int z) {
        // See position hashing algorithm on world format documentation
        // https://github.com/minetest/minetest/blob/master/doc/world_format.md#position-hashing
        Integer pos = z * 16777216 + y * 4096 + x;

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

    /**
     * Places the voxel into this world tile at the specified coordinates.
     * The specified coordinates must be in the coordinate system used by Minetest.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     * @param voxel the voxel to place
     */
    public void set(int x, int y, int z, VoxelType voxel) {
        // (In-Game coords to world coords) XZY => XYZ
        if (!limits().contains(x, z, y)) return;

        getOrCreateBlock(x >> 4, y >> 4, z >> 4).set(x & 0x0f, y & 0x0f, z & 0x0f, (MTVoxelType) voxel);
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minetest.
     */
    @Override
    public void save(File destination) throws MapWriteException {
        SQLiteMapWriter database = new SQLiteMapWriter(destination);
        for (Map.Entry<Integer, Block> entry : blocks.entrySet()) {
            database.insertBlock(entry.getKey(), entry.getValue());
        }
    }
}

package com.ignfab.minalac.generator.outputs.minetest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.outputs.minetest.utils.SQLiteMapWriter;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * Implementation of {@link VoxelTile} for Minetest.
 */
public class MTVoxelTile extends VoxelTile {
    private final File destination;

    private final HashMap<Long, Block> blocks = new HashMap<>();

    /**
     * Creates a new {@code MTVoxelTile}.
     *
     * @param destination Destination database file
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public MTVoxelTile(File destination, WorldBBox3d limits) {
        super(limits);
        this.destination = destination;
    }

    // Retrieves or creates the mapblock corresponding to given voxel position.
    private Block getOrCreateBlock(int blockX, int blockY, int blockZ) {
        Long pos = coordsToPos(blockX, blockY, blockZ);
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

    private long coordsToPos(int blockX, int blockY, int blockZ) {
        // See position hashing algorithm on world format documentation
        // https://github.com/minetest/minetest/blob/master/doc/world_format.md#position-hashing
        return blockZ * 16777216L + blockY * 4096L + blockX;
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
    protected void set(int x, int y, int z, MTVoxel voxel) {
        // (In-Game coords to world coords) XZY => XYZ
        if (!limits().contains(x, z, y)) return;

        getOrCreateBlock(x >> 4, y >> 4, z >> 4).set(x & 0x0f, y & 0x0f, z & 0x0f, voxel);

        // (In-Game coords to world coords) XZY => XYZ
        updateHeightmaps(x, z, y);
    }

    /**
     * {@inheritDoc}
     * This tile is exported in a format for Minetest.
     */
    @Override
    public void save() throws MapWriteException {
        if (destination == null)
            return; // Save disabled if null destination

        SQLiteMapWriter database = new SQLiteMapWriter(destination);
        for (Map.Entry<Long, Block> entry : blocks.entrySet()) {
            database.insertBlock(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     * The returned voxel is not necessarily one placed using {@link Placeable#place}.
     * It may be an air node created when a {@link Block} is initialized.
     */
    @Override
    public Placeable getVoxel(int x, int y, int z) {
        // X/Y/Z => X/Z/Y
        Block block = getBlock(x >> 4, z >> 4, y >> 4);

        if (block == null) return null;

        // X/Y/Z => X/Z/Y
        return block.get(x & 0x0f, z & 0x0f, y & 0x0f);
    }
}

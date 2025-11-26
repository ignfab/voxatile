package com.ignfab.minalac.generator.outputs.minetest;

import java.io.File;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import com.ignfab.minalac.generator.outputs.minetest.utils.SQLiteMapWriter;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * Implementation of {@link VoxelTile} for Minetest.
 */
public class MTVoxelTile extends VoxelTile {

    private final SQLiteMapWriter mapWriter;

    private final Long2ObjectMap<Block> blocks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());

    /**
     * Creates a new {@code MTVoxelTile}.
     *
     * @param mapWriter SQLite writer for map blocks
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public MTVoxelTile(SQLiteMapWriter mapWriter, WorldBBox3d limits) {
        super(limits);
        this.mapWriter = mapWriter;
    }

    // Retrieves or creates the mapblock corresponding to given voxel position.
    private Block getOrCreateBlock(int blockX, int blockY, int blockZ) {
        return blocks.computeIfAbsent(coordsToPos(blockX, blockY, blockZ), k -> new Block());
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


    private static MTMapper mapper = new MTMapper(new File("colors.txt"));

    /**
     * {@inheritDoc}
     * This tile is exported in a format for Minetest.
     */
    @Override
    public void save() throws MapWriteException {
        if (mapWriter == null)
            return; // Save disabled if null writer

        for (Long2ObjectMap.Entry<Block> entry : Long2ObjectMaps.fastIterable(blocks))
            mapWriter.insertBlock(entry.getLongKey(), entry.getValue());

        mapper.saveMinimap(this);
    }

    /**
     * {@inheritDoc}
     * The returned voxel is not necessarily one placed using {@link Placeable#place}.
     * It may be an air node created when the world is initialized.
     * <p>
     * If you try to get a voxel outside the tile limits, it will return {@link MTVoxel#DEFAULT_VOXEL}.
     */
    @Override
    public Placeable getVoxel(int x, int y, int z) {
        // X/Y/Z => X/Z/Y
        Block block = getBlock(x >> 4, z >> 4, y >> 4);

        if (block == null) return MTVoxel.DEFAULT_VOXEL;

        // X/Y/Z => X/Z/Y
        return block.get(x & 0x0f, z & 0x0f, y & 0x0f);
    }


}

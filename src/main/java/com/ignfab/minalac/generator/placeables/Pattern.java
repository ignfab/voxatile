package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A pattern of placeables.
 * <p>
 * Pattern is an unbounded set of voxels covering the entire world.
 */
public interface Pattern extends Placeable {

    /**
     * Returns placeable corresponding to pattern at a given position.
     *
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     *
     * @return placeable, should not be null (return {@code NoVoxel.INSTANCE} instead)
     */
    Placeable get(int x, int y, int z);

    // Default place implementation allowing to use any pattern as placeable.
    @Override
    default void place(VoxelTile tile, int x, int y, int z) {
        get(x, y, z).place(tile, x, y, z);
    }
}

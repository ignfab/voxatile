package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code Placeable} interface represents something placeable in voxel world.
 * @see VoxelType
 */
public interface Placeable {
    /**
     * Places the placeable at given position in {@link VoxelWorld}.
     * Coordinates are expressed using the system used in {@link WorldCoords3d}.
     *
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    void place(int x, int y, int z);

    /**
     * Places the placeable at given position in {@link VoxelWorld}.
     *
     * @param position position where to place the placeable
     */
    default void place(WorldCoords3d position) {
        place(position.x(), position.y(), position.z());
    }
}

package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code Placer} interface places a placeable in in voxel world.
 */
public interface Placer {
    /**
     * Places the placeable at given position in {@link com.ignfab.minalac.generator.world.VoxelWorld}.
     * Coordinates are expressed using the system used in {@link WorldCoords3d}.
     *
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    void place(int x, int y, int z);

    /**
     * Places the placeable at given position in {@link com.ignfab.minalac.generator.world.VoxelWorld}.
     *
     * @param position position where to place the placeable
     */
    default void place(WorldCoords3d position) {
        place(position.x(), position.y(), position.z());
    }
}

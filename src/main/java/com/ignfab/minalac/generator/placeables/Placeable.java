package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * The {@code Placeable} interface represents something placeable in voxel world.
 * @see VoxelType
 */
public interface Placeable {
    /**
     * Places the placeable at given position in {@link VoxelWorld}.
     * Coordinates are expressed using the system used in {@link WorldCoords3d}.
     *
     * @param tile tile to place into
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    void place(VoxelWorldTile tile, int x, int y, int z);

    /**
     * Places the placeable at given position in {@link VoxelWorld}.
     *
     * @param tile tile to place into
     * @param position position where to place the placeable
     */
    default void place(VoxelWorldTile tile, WorldCoords3d position) {
        place(tile, position.x(), position.y(), position.z());
    }
}

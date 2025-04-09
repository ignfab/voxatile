package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * The {@code VoxelType} interface represents a type of voxel that can be placed within a {@link VoxelWorld}.
 */
public interface VoxelType extends Placeable {
    // TODO : Since the javadoc was added, see if it is still relevant to keep the content of doc/legacy/QuickStart.md
    /**
     * Places the voxel in its corresponding {@link VoxelWorld} at the given coordinates.
     * Coordinates are expressed using the system used in {@link com.ignfab.minalac.generator.utils.world3d.WorldCoords3d}.
     * In that system, the z-axis represents the altitude and increments correspond to the size of a voxel.
     * If provided coordinates are outside the limits of the world, the voxel will not be placed.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     */
    @Override
    void place(VoxelWorld world, int x, int y, int z);
}

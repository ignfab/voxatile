package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code VoxelPattern} interface represents a set of voxels arranged in a particular pattern.
 * @see VoxelType
 */
public interface VoxelPattern {
    /**
     * Places the voxels of the pattern in their corresponding {@link VoxelWorld} using the given coordinates as a reference point.
     * Coordinates are expressed using the system used in {@link WorldCoords3d}.
     * In that system, the z-axis represents the altitude and increments correspond to the size of a voxel.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     */
    void place(int x, int y, int z);

    /**
     * Places the voxels of the pattern in their corresponding {@link VoxelWorld} using the given coordinates as a reference point.
     *
     * @param coords the coordinates
     */
    default void place(WorldCoords3d coords) {
        place(coords.x(), coords.y(), coords.z());
    }
}

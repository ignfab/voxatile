package com.ignfab.minalac.generator.world;

/**
 * The {@code EntityType} interface represents a type of entity that can be placed within a {@link VoxelWorld}.
 * An entity differs from a voxel because is can have any size and a position not aligned on the voxels grid.
 */
public interface EntityType {
    /**
     * Places the entity in its corresponding {@link VoxelWorld} at the given coordinates.
     * Coordinates are expressed using the system used in {@link com.ignfab.minalac.generator.utils.world3d.WorldCoords3d}.
     * In that system, the z-axis represents the altitude and increments correspond to the size of a voxel.
     * If provided coordinates are outside the limits of the world, the entity will not be placed.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     */
    void place(double x, double y, double z);
}

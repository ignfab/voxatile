package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * The {@code WorldCoords3d} class represents a component in the voxel world.
 * In the voxel world, coordinates are defined by three axes (x, y, z) where the z-axis represents the altitude, and increments correspond to the size of a voxel.
 * This class is read-only.
 *
 * @param x The x-component value
 * @param y The y-component value
 * @param z The z-component value
 */
public record WorldCoords3d(int x, int y, int z) {
    /**
     * Create a new {@link WorldCoords3d} from an existing {@link WorldCoords2d} and an additional {@code z} value.
     *
     * @param coords an existing {@link WorldCoords2d} object
     * @param z the z-component value
     */
    public WorldCoords3d(WorldCoords2d coords, int z) {
        this(coords.x(), coords.y(), z);
    }

    /**
     * Convert this {@link WorldCoords3d} to {@link WorldCoords2d}, dropping the z-axis.
     *
     * @return a new {@link WorldCoords2d} with current (x, y)
     */
    public WorldCoords2d to2d() {
        return new WorldCoords2d(this);
    }
}

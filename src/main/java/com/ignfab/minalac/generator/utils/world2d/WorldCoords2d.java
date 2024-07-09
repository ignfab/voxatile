package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code WorldCoords2d} class represents a component on the surface (xy-plane) in the voxel world.
 * In the voxel world, coordinates are defined by three axes (x, y, z) where the z-axis represents the altitude, and increments correspond to the size of a voxel.
 * This class is read-only.
 *
 * @param x The x-component value
 * @param y The y-component value
 */
public record WorldCoords2d(int x, int y) {
    /**
     * Create a new {@link WorldCoords2d} from an existing {@link WorldCoords3d}, dropping its {@code z} value.
     *
     * @param coords an existing {@link WorldCoords3d} object
     */
    public WorldCoords2d(WorldCoords3d coords) {
        this(coords.x(), coords.y());
    }

    /**
     * Convert this {@link WorldCoords2d} to {@link WorldCoords3d}, with an additional {@code z} value.
     *
     * @param z the z-component value
     * @return a new {@link WorldCoords3d} with current (x, y) and given z
     */
    public WorldCoords3d to3d(int z) {
        return new WorldCoords3d(this, z);
    }

    /**
     * Create a new {@link WorldCoords2d} from floating point coordinates.
     *
     * @param x The x-component value as double
     * @param y The y-component value as double
     */
    public WorldCoords2d(double x, double y) {
        this(
            (int) Math.floor(x),
            (int) Math.floor(y)
        );
    }
}

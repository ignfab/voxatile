package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;

/**
 * The {@code WorldSize3d} subclass represents the size along the three axes in the voxel world.
 * Each component represents the size along the corresponding axis.
 * Size is always greater than zero.
 *
 * @param x The size along the x-axis
 * @param y The size along the y-axis
 * @param z The size along the z-axis
 *
 * @see WorldCoords3d
 */
public record WorldSize3d(int x, int y, int z) {
    /**
     * Constructs a new {@link WorldSize3d}.
     * Sizes must be greater than 0.
     *
     * @param x the size along the x-axis
     * @param y the size along the y-axis
     * @param z the size along the z-axis
     * @throws IllegalArgumentException if either {@code x}, {@code y} or {@code z} is less than or equal to 0
     */
    public WorldSize3d {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("Invalid size: x, y and z must be positive numbers");
    }

    /**
     * Create a new {@link WorldSize3d} from an existing {@link WorldSize2d} and an additional size along the {@code z}-axis.
     *
     * @param size an existing {@link WorldSize2d} object
     * @param z the size along the z-axis
     */
    public WorldSize3d(WorldSize2d size, int z) {
        this(size.x(), size.y(), z);
    }

    /**
     * Calculates and returns the volume.
     *
     * @return the calculated volume.
     */
    public int volume() {
        return x * y * z;
    }

    /**
     * Convert this {@link WorldSize3d} to {@link WorldSize2d}, dropping the size along the z-axis.
     * The volume represented by this size will be flattened to a 2d surface.
     *
     * @return a new {@link WorldSize2d} with the current size along the x- and y-axes
     */
    public WorldSize2d to2d() {
        return new WorldSize2d(this);
    }
}

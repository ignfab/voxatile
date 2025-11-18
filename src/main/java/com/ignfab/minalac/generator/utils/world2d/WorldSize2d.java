package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

/**
 * The {@code WorldSize2d} subclass represents the size along the x-axis and the y-axis in the voxel world.
 * The x-component represents the size along the x-axis, while the y-component represents the size along the y-axis.
 * Size is always greater than or equals to zero.
 *
 * @param x The size along the x-axis
 * @param y The size along the y-axis
 *
 * @see WorldCoords2d
 */
public record WorldSize2d(int x, int y) {
    /**
     * Constructs a new {@code WorldSize2d}.
     * Sizes must be greater than or equals to 0.
     *
     * @param x the size along the x-axis
     * @param y the size along the y-axis
     * @throws IllegalArgumentException if either {@code x} or {@code y} is less than 0
     */
    public WorldSize2d {
        if (x < 0 || y < 0)
            throw new IllegalArgumentException("Invalid size: x and y must be positive or zero");
    }

    /**
     * Create a new {@link WorldSize2d} from an existing {@link WorldSize3d}, dropping the size along the z-axis.
     * The volume represented by the given size will be flattened to a 2d surface.
     *
     * @param size an existing {@link WorldSize3d} object
     */
    public WorldSize2d(WorldSize3d size) {
        this(size.x(), size.y());
    }

    /**
     * Calculates and returns the area.
     *
     * @return the calculated area.
     */
    public int area() {
        return x * y;
    }

    /**
     * Convert this {@link WorldSize2d} to {@link WorldSize3d}, with an additional size along the {@code z}-axis.
     *
     * @param z the size along the z-axis
     * @return a new {@link WorldSize3d} with the current size along the x- and y-axes
     */
    public WorldSize3d to3d(int z) {
        return new WorldSize3d(this, z);
    }
}

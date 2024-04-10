package com.ignfab.minalac.generator.utils.world2d;

/**
 * The {@code WorldSize2d} subclass represents the size along the x-axis and the y-axis in the voxel world.
 * The x-component represents the size along the x-axis, while the y-component represents the size along the y-axis.
 * Size is always greater than zero.
 *
 * @see com.ignfab.minalac.generator.utils.world2d.WorldCoords2d
 */
public class WorldSize2d extends WorldCoords2d {
    /**
     * Constructs a new {@code WorldSize2d}.
     * Sizes must be greater than 0.
     *
     * @param sizeX the size along the x-axis.
     * @param sizeY the size along the y-axis.
     * @throws IllegalArgumentException if either {@code sizeX} or {@code sizeY} is less than or equal to 0.
     */
    public WorldSize2d(int sizeX, int sizeY) {
        super(sizeX, sizeY);
        if (sizeX <= 0 || sizeY <= 0)
            throw new IllegalArgumentException("Invalid dimensions : sizeX and sizeY must be greater than 0");
    }

    /**
     * Calculates and returns the area.
     *
     * @return the calculated area.
     */
    public int area() {
        return x * y;
    }
}

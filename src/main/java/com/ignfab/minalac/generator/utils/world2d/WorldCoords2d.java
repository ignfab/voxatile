package com.ignfab.minalac.generator.utils.world2d;

/**
 * The {@code WorldCoords2d} class represents a component on the surface (xy-plane) in the voxel world.
 * In the voxel world, coordinates are defined by three axes (x, y, z) where the z-axis represents the altitude, and increments correspond to the size of a voxel.
 * This class is read-only.
 */
public class WorldCoords2d {
    /**
     * The x-component value.
     */
    protected int x;
    /**
     * The y-component value.
     */
    protected int y;

    /**
     * Constructs a new {@code WorldCoords2d}.
     *
     * @param x the x-component value.
     * @param y the y-component value.
     */
    public WorldCoords2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-component value.
     *
     * @return the x-component value.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-component value.
     *
     * @return the y-component value.
     */
    public int getY() {
        return y;
    }
}

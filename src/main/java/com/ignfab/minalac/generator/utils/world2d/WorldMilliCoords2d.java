package com.ignfab.minalac.generator.utils.world2d;

/**
 * A {@code WorldMilliCoords2d} is a {@link WorldCoords2d} but at milli-voxel precision.
 * Values are accessible in milli-voxel precision when prefixed with {@code milli},
 * and approximated to voxel precision without this prefix.
 * Values can also be queried as {@code double} using the {@code real} prefix.
 *
 * @param milliX The x-component value, in milli-voxel.
 * @param milliY The y-component value, in milli-voxel.
 * @see #fromWorldCoords(WorldCoords2d)
 * @see #fromWorldCoords(int, int)
 * @see #fromWorldCoords(double, double)
 */
public record WorldMilliCoords2d(int milliX, int milliY) {
    /**
     * Returns the x-component approximated value, in voxel.
     *
     * @return the x-component approximated value, in voxel.
     */
    public int x() {
        return (int) Math.floor(realX());
    }

    /**
     * Returns the y-component approximated value, in voxel.
     *
     * @return the y-component approximated value, in voxel.
     */
    public int y() {
        return (int) Math.floor(realY());
    }

    /**
     * Returns the x-component floating-point value, in voxel.
     *
     * @return the x-component floating-point value, in voxel.
     */
    public double realX() {
        return fromMilli(milliX);
    }

    /**
     * Returns the y-component floating-point value, in voxel.
     *
     * @return the y-component floating-point value, in voxel.
     */
    public double realY() {
        return fromMilli(milliY);
    }

    /**
     * Converts this milli-voxel coordinate into approximated voxel coordinate.
     *
     * @return a new {@link WorldCoords2d} object with (x, y).
     */
    public WorldCoords2d toWorldCoords() {
        return new WorldCoords2d(x(), y());
    }

    /**
     * Creates a milli-voxel coordinate from voxel coordinate.
     * Milli-voxel precision will be set to 0.
     *
     * @param coords the voxel precision coordinate.
     * @return a new milli-voxel precision coordinate with (x, y).
     */
    public static WorldMilliCoords2d fromWorldCoords(WorldCoords2d coords) {
        return fromWorldCoords(coords.x(), coords.y());
    }

    /**
     * Creates a milli-voxel coordinate from voxel coordinate.
     * Milli-voxel precision will be set to 0.
     *
     * @param x the x-component value, in voxel.
     * @param y the y-component value, in voxel.
     * @return a new milli-voxel precision coordinate with (x, y).
     */
    public static WorldMilliCoords2d fromWorldCoords(int x, int y) {
        return new WorldMilliCoords2d(toMilli(x), toMilli(y));
    }

    /**
     * Creates a milli-voxel coordinate from voxel coordinate.
     * Milli-voxel precision will be set from decimal value.
     *
     * @param x the x-component value, in floating-point voxel.
     * @param y the y-component value, in floating-point voxel.
     * @return a new milli-voxel precision coordinate with (x, y).
     */
    public static WorldMilliCoords2d fromWorldCoords(double x, double y) {
        return new WorldMilliCoords2d(toMilli(x), toMilli(y));
    }

    /**
     * Converts a voxel precision component value to milli-voxel precision.
     * Milli-voxel precision is set to 0.
     *
     * @param value the component value, in voxel.
     * @return the equivalent value, in milli-voxel.
     */
    public static int toMilli(int value) {
        return value * 1000;
    }

    /**
     * Converts a voxel precision component value to milli-voxel precision.
     * Milli-voxel precision is set from decimal value.
     *
     * @param value the component value, in floating-point voxel.
     * @return the equivalent value, in milli-voxel.
     */
    public static int toMilli(double value) {
        return (int) Math.floor(value * 1000);
    }

    /**
     * Converts a milli-voxel precision component value to floating-point voxel precision.
     *
     * @param milliValue the component value, in milli-voxel.
     * @return the equivalent value, in floating-point voxel.
     */
    public static double fromMilli(int milliValue) {
        return milliValue / 1000d;
    }
}

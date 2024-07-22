package com.ignfab.minalac.generator.utils.world3d;

/**
 * A {@code WorldMilliCoords3d} is a {@link WorldCoords3d} but at milli-voxel precision.
 * Values are accessible in milli-voxel precision when prefixed with {@code milli},
 * and approximated to voxel precision without this prefix.
 * Values can also be queried as {@code double} using the {@code real} prefix.
 *
 * @param milliX The x-component value, in milli-voxel.
 * @param milliY The y-component value, in milli-voxel.
 * @param milliZ The z-component value, in milli-voxel.
 * @see #fromWorldCoords(WorldCoords3d)
 * @see #fromWorldCoords(int, int, int)
 * @see #fromWorldCoords(double, double, double)
 */
public record WorldMilliCoords3d(int milliX, int milliY, int milliZ) {
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
     * Returns the z-component approximated value, in voxel.
     *
     * @return the z-component approximated value, in voxel.
     */
    public int z() {
        return (int) Math.floor(realZ());
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
     * Returns the z-component floating-point value, in voxel.
     *
     * @return the z-component floating-point value, in voxel.
     */
    public double realZ() {
        return fromMilli(milliZ);
    }

    /**
     * Converts this milli-voxel coordinate into approximated voxel coordinate.
     *
     * @return a new {@link WorldCoords3d} object with (x, y, z).
     */
    public WorldCoords3d toWorldCoords() {
        return new WorldCoords3d(x(), y(), z());
    }

    /**
     * Creates a milli-voxel coordinate from voxel coordinate.
     * Milli-voxel precision will be set to 0.
     *
     * @param coords the voxel precision coordinate.
     * @return a new milli-voxel precision coordinate with (x, y, z).
     */
    public static WorldMilliCoords3d fromWorldCoords(WorldCoords3d coords) {
        return fromWorldCoords(coords.x(), coords.y(), coords.z());
    }

    /**
     * Creates a milli-voxel coordinate from voxel coordinate.
     * Milli-voxel precision will be set to 0.
     *
     * @param x the x-component value, in voxel.
     * @param y the y-component value, in voxel.
     * @param z the z-component value, in voxel.
     * @return a new milli-voxel precision coordinate with (x, y, z).
     */
    public static WorldMilliCoords3d fromWorldCoords(int x, int y, int z) {
        return new WorldMilliCoords3d(toMilli(x), toMilli(y), toMilli(z));
    }

    /**
     * Creates a milli-voxel coordinate from voxel coordinate.
     * Milli-voxel precision will be set from decimal value.
     *
     * @param x the x-component value, in floating-point voxel.
     * @param y the y-component value, in floating-point voxel.
     * @param z the z-component value, in floating-point voxel.
     * @return a new milli-voxel precision coordinate with (x, y, z).
     */
    public static WorldMilliCoords3d fromWorldCoords(double x, double y, double z) {
        return new WorldMilliCoords3d(toMilli(x), toMilli(y), toMilli(z));
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

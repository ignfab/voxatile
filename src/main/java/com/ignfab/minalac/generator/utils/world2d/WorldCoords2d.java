package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code WorldCoords2d} class represents a component on the surface (xy-plane) in the voxel world.
 * In the voxel world, coordinates are defined by three axes (x, y, z) where the z-axis represents the altitude, and increments correspond to the size of a voxel.
 * This class is read-only.
 *
 * @param x The x-component value
 * @param y The y-component value
 */
public record WorldCoords2d(int x, int y) implements Positioned2d {
    /**
     * Creates a new {@link WorldCoords2d} from an existing {@link Positioned3d}, dropping its {@code z} value.
     *
     * @param position an existing {@link Positioned3d} object
     */
    public WorldCoords2d(Positioned3d position) {
        this(position.coords().x(), position.coords().y());
    }

    /**
     * Converts this {@link WorldCoords2d} to {@link WorldCoords3d}, with an additional {@code z} value.
     *
     * @param z the z-component value
     * @return a new {@link WorldCoords3d} with current (x, y) and given z
     */
    public WorldCoords3d to3d(int z) {
        return new WorldCoords3d(this, z);
    }

    /**
     * Creates a new {@link WorldCoords2d} from double floating point coordinates.
     * Given coordinates are rounded using {@code Math.round}.
     *
     * @param x x-component of coordinates
     * @param y y-component of coordinates
     *
     * @return resulting {@link WorldCoords2d}.
     */
    public static WorldCoords2d round(double x, double y) {
        return new WorldCoords2d((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Creates a new {@link WorldCoords2d} from double floating point coordinates.
     * Given coordinates are floored using {@code Math.floor}.
     *
     * @param x x-component of coordinates
     * @param y y-component of coordinates
     *
     * @return resulting {@link WorldCoords2d}.
     */
    public static WorldCoords2d floor(double x, double y) {
        return new WorldCoords2d((int) Math.floor(x), (int) Math.floor(y));
    }

    /**
     * Creates a new {@link WorldCoords2d} from double floating point coordinates.
     * Given coordinates are ceiled using {@code Math.ceil}.
     *
     * @param x x-component of coordinates
     * @param y y-component of coordinates
     *
     * @return resulting {@link WorldCoords2d}.
     */
    public static WorldCoords2d ceil(double x, double y) {
        return new WorldCoords2d((int) Math.ceil(x), (int) Math.ceil(y));
    }

    @Override
    public WorldCoords2d coords() {
        return this;
    }
}

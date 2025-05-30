package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
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
public record WorldCoords3d(int x, int y, int z) implements Positioned3d {
    /**
     * Creates a new {@link WorldCoords3d} from an existing {@link Positioned2d} and an additional {@code z} value.
     *
     * @param position an existing {@link Positioned2d} object
     * @param z the z-component value
     */
    public WorldCoords3d(Positioned2d position, int z) {
        this(position.coords().x(), position.coords().y(), z);
    }

    /**
     * Converts this {@link WorldCoords3d} to {@link WorldCoords2d}, dropping the z-axis.
     *
     * @return a new {@link WorldCoords2d} with current (x, y)
     */
    public WorldCoords2d to2d() {
        return new WorldCoords2d(this);
    }

   /**
     * Creates a new {@link WorldCoords3d} from double floating point coordinates.
     * Given coordinates are rounded using {@link Math#round}.
     *
     * @param x x-component of coordinates
     * @param y y-component of coordinates
     * @param z z-component of coordinates
     */
    public static WorldCoords3d round(double x, double y, double z) {
        return new WorldCoords3d((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }
    /**
     * Creates a new {@link WorldCoords3d} from double floating point coordinates.
     * Given coordinates are floored using {@link Math#floor}.
     *
     * @param x x-component of coordinates
     * @param y y-component of coordinates
     * @param z z-component of coordinates
     *
     * @return resulting {@link WorldCoords3d}.
     */
    public static WorldCoords3d floor(double x, double y, double z) {
        return new WorldCoords3d((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    /**
     * Creates a new {@link WorldCoords3d} from double floating point coordinates.
     * Given coordinates are ceiled using {@link Math#ceil}.
     *
     * @param x x-component of coordinates
     * @param y y-component of coordinates
     * @param z z-component of coordinates
     *
     * @return resulting {@link WorldCoords3d}.
     */
    public static WorldCoords3d ceil(double x, double y, double z) {
        return new WorldCoords3d((int) Math.ceil(x), (int) Math.ceil(y), (int) Math.ceil(z));
    }

    /**
     * Returns addition result with given coordinates.
     *
     * @param x x-component of value to add
     * @param y y-component of value to add
     * @param z z-component of value to add
     * @return a new {@code WorldCoords3d} resulting of the addition.
     */
    public WorldCoords3d add(int x, int y, int z) {
        return new WorldCoords3d(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Returns addition result with given value.
     *
     * @param value value to add
     * @return a new {@code WorldCoords3d} resulting of the addition.
     */
    public WorldCoords3d add(WorldCoords3d value) {
        return add(value.x, value.y, value.z);
    }

    @Override
    public WorldCoords3d coords() {
        return this;
    }
}

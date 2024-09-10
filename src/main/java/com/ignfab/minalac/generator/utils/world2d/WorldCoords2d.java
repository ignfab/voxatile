package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

/**
 * The {@code WorldCoords2d} class represents a component on the surface (xy-plane) in the voxel world.
 * In the voxel world, coordinates are defined by three axes (x, y, z) where the z-axis represents the altitude, and increments correspond to the size of a voxel.
 * This class is read-only.
 *
 * @param x The x-component value
 * @param y The y-component value
 */
public record WorldCoords2d(int x, int y) implements Voxel2d {
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
     * Create a new {@link WorldCoords2d} from double floating point coordinates.
     * Given coordinates are {@code Math.round}ed.
     *
     * @param x x-component of coordinates
     * @param y y-compoment of coordinates
     *
     * @return resulting {@WorldCoords2d}.
     */
    public static WorldCoords2d round(double x, double y) {
        return new WorldCoords2d((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Create a new {@link WorldCoords2d} from double floating point coordinates.
     * Given coordinates are {@code Math.floor}ed.
     *
     * @param x x-component of coordinates
     * @param y y-compoment of coordinates
     *
     * @return resulting {@WorldCoords2d}.
     */
    public static WorldCoords2d floor(double x, double y) {
        return new WorldCoords2d((int) Math.floor(x), (int) Math.floor(y));
    }

    /**
     * Create a new {@link WorldCoords2d} from double floating point coordinates.
     * Given coordinates are {@code Math.ceil}ed.
     *
     * @param x x-component of coordinates
     * @param y y-compoment of coordinates
     *
     * @return resulting {@WorldCoords2d}.
     */
    public static WorldCoords2d ceil(double x, double y) {
        return new WorldCoords2d((int) Math.ceil(x), (int) Math.ceil(y));
    }

    @Override
    public WorldCoords2d coords() {
        return this;
    }
}

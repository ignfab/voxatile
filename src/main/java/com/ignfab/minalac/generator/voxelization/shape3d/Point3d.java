package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Represents a 3d point in the voxel world.
 *
 * @param coords the coordinate of the point.
 */
public record Point3d(WorldCoords3d coords) implements Positioned3d, Shape3d {

    /**
     * Creates a new point from integer coordinates.
     * This is a shortcut to {@code new Point3d(new WorldCoords3d(x, y, z))}.
     *
     * @param x The x-component value
     * @param y The y-component value
     * @param z The z-component value
     */
    public Point3d(int x, int y, int z) {
        this(new WorldCoords3d(x, y, z));
    }

    @Override
    public Iterable<Point3d> points() {
        return Collections.singleton(this);
    }

    @Override
    public Iterable<LineString3d> lineStrings() {
        return Collections::emptyIterator;
    }

    @Override
    public Iterable<Polygon3d> polygons() {
        return Collections::emptyIterator;
    }

    @Override
    public WorldBBox3d bbox() {
        return new WorldBBox3d(coords);
    }
}

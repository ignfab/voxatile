package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d point in the voxel world.
 *
 * @param coords the coordinate of the point.
 */
public record Point2d(WorldCoords2d coords) implements Positioned2d, Shape2d {

    /**
     * Creates a new point from integer coordinates.
     * This is a shortcut to {@code new Point2d(new WorldCoords2d(x, y))}.
     *
     * @param x The x-component value
     * @param y The y-component value
     */
    public Point2d(int x, int y) {
        this(new WorldCoords2d(x, y));
    }

    @Override
    public Iterable<Point2d> points() {
        return Collections.singleton(this);
    }

    @Override
    public Iterable<LineString2d> lineStrings() {
        return Collections::emptyIterator;
    }

    @Override
    public Iterable<Polygon2d> polygons() {
        return Collections::emptyIterator;
    }

    @Override
    public WorldBBox2d bbox() {
        return new WorldBBox2d(coords);
    }
}

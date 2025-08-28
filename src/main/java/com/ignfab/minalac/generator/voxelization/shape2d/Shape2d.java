package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Collections;

/**
 * Interface for voxel shapes in 2 dimensions.
 */
public interface Shape2d extends Shape2dConvertible {
    /**
     * Returns an iterable over all points in this shape.
     *
     * @return iterable over all points.
     */
    default Iterable<Point2d> points() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an iterable over all lines in this shape.
     *
     * @return iterable over all lines.
     */
    default Iterable<Line2d> lines() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an iterable over all line strings in this shape.
     *
     * @return iterable over all line strings.
     */
    default Iterable<LineString2d> lineStrings() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an iterable over all polygons in this shape.
     *
     * @return iterable over all line strings.
     */
    default Iterable<Polygon2d> polygons() {
        return Collections::emptyIterator;
    }

    @Override
    default Shape2d toShape2d() {
        return this;
    }
}

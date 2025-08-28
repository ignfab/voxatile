package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Point2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Polygon2dIterator;

/**
 * A voxelizer for surfacic shapes.
 */
public class SurfaceVoxelizer2d {
    /**
     * Voxelizes a point.
     *
     * @param point Point to voxelize
     * @return an iterable over voxelized position.
     */
    public Iterable<Positioned2d> voxelize(Point2d point) {
        return Iterables.singleton(point);
    }

    /**
     * Voxelizes a polygon.
     *
     * @param polygon Polygon to voxelize
     * @return an iterable over voxelized positions.
     */
    public Iterable<Positioned2d> voxelize(Polygon2d polygon) {
        return () -> new Polygon2dIterator(polygon, true);
    }

    /**
     * Voxelizes any other shapes, using polygon voxelization.
     * <p>
     * Lines and points will be ignored, only surfaces are drawn.
     *
     * @param convertible Something convertible to a {@code Shape2d}.
     * @return an iterable over voxelized positions.
     */
    public Iterable<Positioned2d> voxelize(Shape2dConvertible convertible) {
        Shape2d shape = convertible.toShape2d();
        return Iterables.union(
            Iterables.unwrap(Iterables.remap(shape.points(), this::voxelize)),
            Iterables.unwrap(Iterables.remap(shape.polygons(), this::voxelize))
        );
    }

}

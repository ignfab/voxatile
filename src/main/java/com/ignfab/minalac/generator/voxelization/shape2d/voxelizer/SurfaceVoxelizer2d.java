package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Polygon2dIterator;

/**
 * A voxelizer for surfacic shapes.
 */
public class SurfaceVoxelizer2d implements Shape2dVoxelizer {
    /**
     * Voxelizes a polygon.
     *
     * @param polygon Polygon to voxelize
     * @return an iterable over voxelized positions.
     */
    public Iterable<Positioned2d> voxelize(Polygon2d polygon) {
        return () -> new Polygon2dIterator(polygon, true);
    }

    @Override
    public Iterable<? extends Positioned2d> voxelizeShape2d(Shape2d shape) {
        return Iterables.flatMap(shape.polygons(), this::voxelize);
    }

}

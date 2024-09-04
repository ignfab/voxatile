package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Collections;

/**
 * Represents a 2d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point2d implements Shape2d {
    // Point is stored as a bbox to avoid extra instanciations
    private final WorldBBox2d bbox;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point2d(WorldCoords2d coords) {
        this.bbox = new WorldBBox2d(coords, new WorldSize2d(1, 1));
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public Iterable<LineVoxel2d> borderVoxels() {
        return () -> new SingletonIterator<>(new LineVoxel2d(bbox.getMin(), null, 0));
    }

    @Override
    public Iterable<Voxel2d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }
}

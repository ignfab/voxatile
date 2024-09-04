package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Collections;

/**
 * Represents a 2d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point2d implements Shape2d {
    private final WorldCoords2d coords;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point2d(WorldCoords2d coords) {
        this.coords = coords;
    }

    @Override
    public Iterable<LineVoxel2d> borderVoxels() {
        return () -> new SingletonIterator<>(new LineVoxel2d(coords, null, 0));
    }

    @Override
    public Iterable<Voxel2d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }
}

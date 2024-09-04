package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

import java.util.Collections;

/**
 * Represents a 3d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point3d implements Shape3d {
    private final WorldCoords3d coords;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point3d(WorldCoords3d coords) {
        this.coords = coords;
    }

    @Override
    public Iterable<LineVoxel3d> borderVoxels() {
        return () -> new SingletonIterator<>(new LineVoxel3d(coords, null, 0));
    }

    @Override
    public Iterable<Voxel3d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }
}

package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

import java.util.Collections;

/**
 * Represents a 3d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point3d implements Shape3d {
    // Point is stored as a bbox to avoid extra instanciations
    private final WorldBBox3d bbox;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point3d(WorldCoords3d coords) {
        this.bbox = new WorldBBox3d(coords, new WorldSize3d(1, 1, 1));
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public Iterable<LineVoxel3d> borderVoxels() {
        return () -> new SingletonIterator<>(new LineVoxel3d(bbox.getMin(), null, 0));
    }

    @Override
    public Iterable<Voxel3d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }
}

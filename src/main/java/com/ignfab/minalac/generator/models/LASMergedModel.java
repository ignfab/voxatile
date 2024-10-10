package com.ignfab.minalac.generator.models;

import java.util.HashSet;
import java.util.Set;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * Model containing multiple lidar points sharing the same metadata.
 * This model is more suitable when dealing with large amount of points.
 * @see LASPointModel
 */
public class LASMergedModel extends ModelImpl implements Voxelizable2d, Voxelizable3d {
    private final Set<WorldCoords3d> points = new HashSet<>();

    /**
     * Adds a point to the model.
     * Note: If two points are in the same voxel, only one will be kept.
     * @param p the point to add
     */
    public void addPoint(WorldCoords3d p) {
        points.add(p);
    }

    @Override
    public String salt() {
        // TODO: Same issue than with matrix model
        throw new UnsupportedOperationException("Unimplemented method 'salt'");
    }

    @Override
    public Voxelizer2d voxelize2d(WorldBBox2d bbox) {
        return () -> bbox.crop(Iterators.remap(points.iterator(), WorldCoords3d::to2d));
    }

    @Override
    public Voxelizer3d voxelize3d(WorldBBox3d bbox) {
        return () -> bbox.crop(points.iterator());
    }
}

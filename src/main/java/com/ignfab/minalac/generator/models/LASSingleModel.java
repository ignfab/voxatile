package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

import java.util.ArrayList;
import java.util.List;

public class LASSingleModel extends Model implements Voxelizable2d, Voxelizable3d {
    private final List<WorldCoords3d> points = new ArrayList<>();

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

package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ConnectedLine2dIterator;

public class ConnectedLine2dVoxelizer implements Shape2dVoxelizer {

    double delta;

    public ConnectedLine2dVoxelizer(double delta) {
        this.delta = delta;
    }

    public Iterable<Positioned2d> voxelize(Segment2d segment) {
        return () -> new ConnectedLine2dIterator(segment, true);
    }

    public Iterable<Positioned2d> voxelize(LineString2d linestring) {
        return Iterables.unwrap(Iterables.remap(linestring.shifted(delta).segments(), this::voxelize));
    }

    @Override
    public Iterable<Positioned2d> voxelize(Shape2dConvertible convertible) {
        return Iterables.unwrap(Iterables.remap(convertible.toShape2d().lineStrings(), this::voxelize));
    }

}

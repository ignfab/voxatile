package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.ShapesVoxelizer3d;

/**
 * A model voxelizable in 3d consisting of a simple line.
 */
public class TestingLineShapeVoxelizable3dModel extends ModelImpl implements ShapesVoxelizable3d {
    private final WorldCoords3d start;
    private final WorldCoords3d end;

    /**
     * Creates a new model with given start and end positions.
     *
     * @param start starting position of the line
     * @param end ending position of the line
     */
    public TestingLineShapeVoxelizable3dModel(WorldCoords3d start, WorldCoords3d end) {
        super();
        this.start = start;
        this.end = end;
    }

    @Override
    public ShapesVoxelizer3d voxelize3d(WorldBBox3d limits) {
        ShapesVoxelizer3d voxelizer = new ShapesVoxelizer3d(limits);
        voxelizer.addShape(new Line3d(start, end));
        return voxelizer;
    }

    @Override
    public String salt() {
        return "";
    }
}

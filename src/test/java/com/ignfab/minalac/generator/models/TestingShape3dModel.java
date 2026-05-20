package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;

/**
 * A shape3d convertible model out of a {@link Shape3d} for testing purposes.
 */
public class TestingShape3dModel extends ModelImpl implements Shape3dConvertibleModel {
    private final Shape3d shape;

    /**
     * Creates a new model with given shape.
     *
     * @param shape shape for this model
     */
    public TestingShape3dModel(Shape3d shape) {
        this.shape = shape;
    }

    @Override
    public Shape3d toShape3d() {
        return shape;
    }

    @Override
    public String salt() {
        return "";
    }
}

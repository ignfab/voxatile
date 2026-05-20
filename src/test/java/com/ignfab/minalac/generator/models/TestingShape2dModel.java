package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;

/**
 * A Shape2d convertible model out of a {@link Shape2d} for testing purposes.
 */
public class TestingShape2dModel extends ModelImpl implements Shape2dConvertibleModel {
    private final Shape2d shape;

    /**
     * Creates a new model with given shape.
     *
     * @param shape shape for this model
     */
    public TestingShape2dModel(Shape2d shape) {
        this.shape = shape;
    }

    @Override
    public Shape2d toShape2d() {
        return shape;
    }

    @Override
    public String salt() {
        return "";
    }
}

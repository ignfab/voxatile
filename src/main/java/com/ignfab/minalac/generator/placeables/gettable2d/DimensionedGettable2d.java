package com.ignfab.minalac.generator.placeables.gettable2d;

import com.ignfab.minalac.generator.placeables.Placeable;

/**
 * Represents a dimensioned {@link Gettable2d}.
 */
public abstract class DimensionedGettable2d implements Gettable2d {
    /**
     * Length along the first axis.
     */
    protected int sizeFirstAxis;
    /**
     * Length along the second axis.
     */
    protected int sizeSecondAxis;

    /**
     * Creates a new {@link DimensionedGettable2d}.
     *
     * @param sizeFirstAxis the size along the first axis
     * @param sizeSecondAxis the size along the second axis
     */
    protected DimensionedGettable2d(int sizeFirstAxis, int sizeSecondAxis) {
        this.sizeFirstAxis = sizeFirstAxis;
        this.sizeSecondAxis = sizeSecondAxis;
    }

    @Override
    public abstract Placeable get(int u, int v);
}

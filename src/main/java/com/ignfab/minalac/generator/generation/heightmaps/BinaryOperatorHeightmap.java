package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * Abstract class for all readable heightmaps that are the result of an operation on two other {@code ReadableHeightmap}.
 */
public abstract class BinaryOperatorHeightmap implements ReadableHeightmap {
    /**
     * The first heightmap.
     */
    protected final ReadableHeightmap firstOperand;
    /**
     * The second heightmap.
     */
    protected final ReadableHeightmap secondOperand;

    protected BinaryOperatorHeightmap(ReadableHeightmap firstOperand, ReadableHeightmap secondOperand) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    @Override
    public WorldBBox2d bbox() {
        return firstOperand.bbox().intersection(secondOperand.bbox());
    }
}

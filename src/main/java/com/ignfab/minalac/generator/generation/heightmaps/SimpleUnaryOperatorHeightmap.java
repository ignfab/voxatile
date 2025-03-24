package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.function.IntUnaryOperator;

/**
 * Applies a function to all values of the provided heightmap.
 */
public class SimpleUnaryOperatorHeightmap extends UnaryOperatorHeightmap {
    private final IntUnaryOperator operator;

    /**
     * Creates a new {@link SimpleUnaryOperatorHeightmap}.
     *
     * @param base the base heightmap
     * @param operator the function which will be applied to the heightmap
     */
    public SimpleUnaryOperatorHeightmap(ReadableHeightmap base, IntUnaryOperator operator) {
        super(base);
        this.operator = operator;
    }

    @Override
    public int get(int x, int y) {
        return operator.applyAsInt(base.get(x, y));
    }
}

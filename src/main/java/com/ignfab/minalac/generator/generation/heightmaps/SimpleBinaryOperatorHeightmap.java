package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.function.IntBinaryOperator;

/**
 * Applies a function by taking the values of the provided heightmaps as arguments.
 */
public class SimpleBinaryOperatorHeightmap extends BinaryOperatorHeightmap {
    private final IntBinaryOperator biFunction;

    /**
     * Creates a new {@link SimpleBinaryOperatorHeightmap}.
     *
     * @param first the first heightmap.
     * @param second the second heightmap.
     * @param operator the operator that will be applied.
     */
    public SimpleBinaryOperatorHeightmap(ReadableHeightmap first, ReadableHeightmap second, IntBinaryOperator operator) {
        super(first, second);
        this.biFunction = operator;
    }

    @Override
    public int get(int x, int y) {
        return biFunction.applyAsInt(firstOperand.get(x, y), secondOperand.get(x, y));
    }
}

package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import java.util.function.IntBinaryOperator;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

/**
 * An operator on two heightmaps.
 */
public interface BinaryHeightmapOperator {

    /**
     * Computes operation result at (x, y) for given operands.
     *
     * @param x x-coordinate of the wanted point
     * @param y y-coordinate of the wanted point
     * @param firstOperand first heightmap operand
     * @param secondOperand second heightmap operand
     * @return Operation result at (x, y)
     */
    int compute(int x, int y, ReadableHeightmap firstOperand, ReadableHeightmap secondOperand);

    /**
     * A simple {@code BinaryHeightmapOperator} based on an {@code IntBinaryOperator}.
     */
    class Simple implements BinaryHeightmapOperator {
        private final IntBinaryOperator operator;

        /**
         * Creates a new {@link BinaryHeightmapOperator.Simple}.
         *
         * @param operator the operator that will be applied.
         */
        public Simple(IntBinaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public int compute(int x, int y, ReadableHeightmap firstOperand, ReadableHeightmap secondOperand) {
            return operator.applyAsInt(firstOperand.get(x, y), secondOperand.get(x, y));
        }
    }
}

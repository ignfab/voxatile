package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import java.util.function.IntUnaryOperator;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

/**
 * An operator on a heightmap.
 */
public interface UnaryHeightmapOperator {

    /**
     * Computes operation result at (x, y) for given operand.
     *
     * @param x x-coordinate of the wanted point
     * @param y y-coordinate of the wanted point
     * @param operand heightmap operand
     * @return Operation result at (x, y)
     */
    int compute(int x, int y, ReadableHeightmap operand);

   /**
     * A simple {@code UnaryHeightmapOperator} based on an {@code IntUnaryOperator}.
     */
    class Simple implements UnaryHeightmapOperator {
        private final IntUnaryOperator operator;

        /**
         * Creates a new {@link UnaryHeightmapOperator.Simple}.
         *
         * @param operator the operator that will be applied.
         */
        public Simple(IntUnaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public int compute(int x, int y, ReadableHeightmap operand) {
            return operator.applyAsInt(operand.get(x, y));
        }
    }
}

package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A {@code ReadableHeightmap} resulting of an operation between two heightmaps.
 *
 * This gives a {@link UnboundReadableHeightmap} that has to be bound to a {@link GenerationTile} in order to be used.
 */
public class BinaryOperatorHeightmap implements UnboundReadableHeightmap {
    private final UnboundReadableHeightmap firstOperand;
    private final UnboundReadableHeightmap secondOperand;
    private final BinaryHeightmapOperator operator;

    /**
     * Creates a new {@code BinaryOperatorHeightmap}.
     *
     * @param firstOperand First heightmap operand
     * @param secondOperand Second heightmap operand
     * @param operator Heightmap operator
     */
    public BinaryOperatorHeightmap(UnboundReadableHeightmap firstOperand, UnboundReadableHeightmap secondOperand, BinaryHeightmapOperator operator) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operator = operator;
    }

    @Override
    public ReadableHeightmap bind(GenerationTile tile) {
        return new Bound(firstOperand.bind(tile), secondOperand.bind(tile), operator);
    }

    /**
     * Bound version of {@code BinaryOperatorHeightmap}.
     */
    public final class Bound implements ReadableHeightmap {
        private final ReadableHeightmap firstOperand;
        private final ReadableHeightmap secondOperand;
        private final BinaryHeightmapOperator operator;

        private Bound(ReadableHeightmap firstOperand, ReadableHeightmap secondOperand, BinaryHeightmapOperator operator) {
            this.firstOperand = firstOperand;
            this.secondOperand = secondOperand;
            this.operator = operator;
        }

        @Override
        public WorldBBox2d bbox() {
            return operator.bbox(firstOperand, secondOperand);
        }

        @Override
        public int get(int x, int y) {
            return operator.compute(x, y, firstOperand, secondOperand);
        }

    }
}

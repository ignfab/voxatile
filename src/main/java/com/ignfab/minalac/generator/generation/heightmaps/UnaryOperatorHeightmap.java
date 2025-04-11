package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A {@code ReadableHeightmap} resulting of an operation on a heightmap.
 *
 * This gives a {@link UnboundReadableHeightmap} that has to be bound to a {@link GenerationTile} in order to be used.
 */
public class UnaryOperatorHeightmap implements UnboundReadableHeightmap {
    private final UnboundReadableHeightmap operand;
    private final UnaryHeightmapOperator operator;

    /**
     * Creates a new {@code UnaryOperatorHeightmap}.
     *
     * @param operand Heightmap operand
     * @param operator Heightmap operator
     */
    public UnaryOperatorHeightmap(UnboundReadableHeightmap operand, UnaryHeightmapOperator operator) {
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public ReadableHeightmap bind(GenerationTile tile) {
        return new Bound(operand.bind(tile), operator);
    }

    /**
     * Bound version of {@code UnaryOperatorHeightmap}.
     */
    public final class Bound implements ReadableHeightmap {
        private final ReadableHeightmap operand;
        private final UnaryHeightmapOperator operator;

        private Bound(ReadableHeightmap operand, UnaryHeightmapOperator operator) {
            this.operand = operand;
            this.operator = operator;
        }

        @Override
        public WorldBBox2d bbox() {
            return operator.bbox(operand);
        }

        @Override
        public int get(int x, int y) {
            return operator.compute(x, y, operand);
        }
    }
}

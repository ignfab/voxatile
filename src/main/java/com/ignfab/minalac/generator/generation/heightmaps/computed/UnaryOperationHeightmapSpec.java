package com.ignfab.minalac.generator.generation.heightmaps.computed;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.operators.UnaryHeightmapOperator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A {@code ReadableHeightmapSpec} representing an operation on a heightmap.
 */
public class UnaryOperationHeightmapSpec extends ReadableHeightmapSpec {
    private final ReadableHeightmapSpec operandSpec;
    private final UnaryHeightmapOperator operator;

    /**
     * Creates a new {@code UnaryOperationHeightmapSpec}.
     *
     * @param operandSpec Spec of heightmap operand
     * @param operator Heightmap operator
     */
    public UnaryOperationHeightmapSpec(ReadableHeightmapSpec operandSpec, UnaryHeightmapOperator operator) {
        this.operandSpec = operandSpec;
        this.operator = operator;
    }

    @Override
    protected ReadableHeightmap create(HeightmapStore store) {
        return new Created(store.get(operandSpec), operator);
    }

    /**
     * Usable heightmap corresponding to this {@code UnaryOperationHeightmapSpec}.
     *
     * @see ReadableHeightmapSpec
     */
    private static final class Created implements ReadableHeightmap {
        private final ReadableHeightmap operand;
        private final UnaryHeightmapOperator operator;

        private Created(ReadableHeightmap operand, UnaryHeightmapOperator operator) {
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

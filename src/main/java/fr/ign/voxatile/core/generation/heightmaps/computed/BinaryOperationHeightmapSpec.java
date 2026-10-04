package fr.ign.voxatile.core.generation.heightmaps.computed;

import fr.ign.voxatile.core.generation.heightmaps.HeightmapStore;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.generation.heightmaps.computed.operators.BinaryHeightmapOperator;
import fr.ign.voxatile.core.utils.world2d.WorldBBox2d;

/**
 * A {@code ReadableHeightmapSpec} representing an operation between two heightmaps.
 */
public class BinaryOperationHeightmapSpec extends ReadableHeightmapSpec {
    private final ReadableHeightmapSpec firstOperandSpec;
    private final ReadableHeightmapSpec secondOperandSpec;
    private final BinaryHeightmapOperator operator;

    /**
     * Creates a new {@code BinaryOperationHeightmapSpec}.
     *
     * @param firstOperandSpec Spec of first heightmap operand
     * @param secondOperandSpec Spec of second heightmap operand
     * @param operator Heightmap operator
     */
    public BinaryOperationHeightmapSpec(ReadableHeightmapSpec firstOperandSpec, ReadableHeightmapSpec secondOperandSpec, BinaryHeightmapOperator operator) {
        this.firstOperandSpec = firstOperandSpec;
        this.secondOperandSpec = secondOperandSpec;
        this.operator = operator;
    }

    @Override
    protected ReadableHeightmap create(HeightmapStore store) {
        return new Created(store.get(firstOperandSpec), store.get(secondOperandSpec), operator);
    }

    /**
     * Usable heightmap corresponding to this {@code BinaryOperationHeightmapSpec}.
     *
     * @see ReadableHeightmapSpec
     */
    private final class Created implements ReadableHeightmap {
        private final ReadableHeightmap firstOperand;
        private final ReadableHeightmap secondOperand;
        private final BinaryHeightmapOperator operator;

        private Created(ReadableHeightmap firstOperand, ReadableHeightmap secondOperand, BinaryHeightmapOperator operator) {
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

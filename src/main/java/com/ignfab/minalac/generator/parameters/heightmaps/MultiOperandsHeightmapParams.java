package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntBinaryOperator;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.SimpleBinaryOperatorHeightmap;

/**
 * Abstract class for applying successive operation on a list of heightmaps.
 */
public abstract class MultiOperandsHeightmapParams implements ReadableHeightmapParams {
    private final List<ReadableHeightmapParams> operands;
    private final IntBinaryOperator operator;

    protected MultiOperandsHeightmapParams(List<ReadableHeightmapParams> operands, IntBinaryOperator operator) {
        this.operands = operands;
        this.operator = operator;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (operands.isEmpty())
            throw new IllegalArgumentException("There must be at least one heightmap operand");

        operands.forEach(ReadableHeightmapParams::validate);
    }

    @Override
    public ReadableHeightmap create(Generation generation) {
        Iterator<ReadableHeightmapParams> iterator = operands.iterator();

        ReadableHeightmap heightmap = iterator.next().create(generation);

        while (iterator.hasNext())
            heightmap = new SimpleBinaryOperatorHeightmap(heightmap, iterator.next().create(generation), operator);

        return heightmap;
    }

    /**
     * Sums each height of the provided heightmaps.
     */
    public static class Sum extends MultiOperandsHeightmapParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         *
         * @param sum list of heightmaps.
         */
        @ConstructorProperties("sum")
        public Sum(List<ReadableHeightmapParams> sum) {
            super(sum, Integer::sum);
        }
    }

    /**
     * Multiplies each height of the provided heightmaps.
     */
    public static class Product extends MultiOperandsHeightmapParams {
        /**
         * The list of heightmaps to product (required).
         *
         * @param product list of heightmaps.
         */
        @ConstructorProperties("product")
        public Product(List<ReadableHeightmapParams> product) {
            super(product, (a, b) -> a * b);
        }
    }
}

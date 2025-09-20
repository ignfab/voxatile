package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.BinaryOperationModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * Abstract class for applying binary operator on a list of model values.
 */
public abstract class MultiOperandsModelValueParams extends ModelValueParams {
    private final List<ModelValueParams> operands;
    private final DoubleBinaryOperator operator;

    protected MultiOperandsModelValueParams(List<ModelValueParams> operands, DoubleBinaryOperator operator) {
        this.operands = operands;
        this.operator = operator;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (operands.isEmpty())
            throw new IllegalArgumentException("There must be at least one model value operand");

        operands.forEach(ModelValueParams::validate);
    }

    @Override
    public ModelValue create(Generation generation) {
        Iterator<ModelValueParams> iterator = operands.iterator();

        ModelValue modelValue = iterator.next().create(generation);

        while (iterator.hasNext())
            modelValue = new BinaryOperationModelValue(modelValue, iterator.next().create(generation), operator);

        return modelValue;
    }

    /**
     * Sums each value of the provided model values.
     */
    public static class Sum extends MultiOperandsModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param sum list of model values
         */
        @ConstructorProperties("sum")
        public Sum(List<ModelValueParams> sum) {
            super(sum, Double::sum);
        }
    }

    /**
     * Multiplies each value of the provided model values.
     */
    public static class Product extends MultiOperandsModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param product list of model values
         */
        @ConstructorProperties("product")
        public Product(List<ModelValueParams> product) {
            super(product, (a, b) -> a * b);
        }
    }

    /**
     * Keeps the lowest value of the provided model values.
     */
    public static class Lowest extends MultiOperandsModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param lowest list of model values
         */
        @ConstructorProperties("lowest")
        public Lowest(List<ModelValueParams> lowest) {
            super(lowest, Math::min);
        }
    }

    /**
     * Keeps the highest value of the provided model values.
     */
    public static class Highest extends MultiOperandsModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param highest list of model values
         */
        @ConstructorProperties("highest")
        public Highest(List<ModelValueParams> highest) {
            super(highest, Math::max);
        }
    }
}

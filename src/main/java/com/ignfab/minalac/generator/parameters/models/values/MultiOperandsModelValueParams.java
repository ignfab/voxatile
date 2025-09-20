package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.BinaryOperationModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

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

    public static class Sum extends MultiOperandsModelValueParams {
        @ConstructorProperties("sum")
        public Sum(List<ModelValueParams> sum) {
            super(sum, Double::sum);
        }
    }

    public static class Product extends MultiOperandsModelValueParams {
        @ConstructorProperties("product")
        public Product(List<ModelValueParams> product) {
            super(product, (a, b) -> a * b);
        }
    }
}

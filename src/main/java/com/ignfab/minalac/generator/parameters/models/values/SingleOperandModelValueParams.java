package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;
import java.util.function.DoubleUnaryOperator;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.models.values.UnaryOperationModelValue;

public abstract class SingleOperandModelValueParams extends ModelValueParams {
    private final ModelValueParams operand;
    private final DoubleUnaryOperator operator;

    protected SingleOperandModelValueParams(ModelValueParams operand, DoubleUnaryOperator operator) {
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        operand.validate();
    }

    @Override
    public ModelValue create(Generation generation) {
        return new UnaryOperationModelValue(operand.create(generation), operator);
    }

    public static class Round extends SingleOperandModelValueParams {
        @ConstructorProperties("round")
        public Round(ModelValueParams round) {
            super(round, Math::round);
        }
    }

    public static class Floor extends SingleOperandModelValueParams {
        @ConstructorProperties("floor")
        public Floor(ModelValueParams floor) {
            super(floor, Math::floor);
        }
    }

    public static class Ceil extends SingleOperandModelValueParams {
        @ConstructorProperties("ceil")
        public Ceil(ModelValueParams ceil) {
            super(ceil, Math::ceil);
        }
    }
}

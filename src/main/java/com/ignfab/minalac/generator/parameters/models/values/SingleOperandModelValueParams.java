package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;
import java.util.function.DoubleUnaryOperator;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.models.values.UnaryOperationModelValue;

/**
 * Abstract class for applying unary operator on a model value.
 */
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

    /**
     * Rounds the provided model value using {@link Math#round(double)}.
     */
    public static class Round extends SingleOperandModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param round model value to round
         */
        @ConstructorProperties("round")
        public Round(ModelValueParams round) {
            super(round, Math::round);
        }
    }

    /**
     * Floors the provided model value using {@link Math#floor(double)}.
     */
    public static class Floor extends SingleOperandModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param floor model value to floor
         */
        @ConstructorProperties("floor")
        public Floor(ModelValueParams floor) {
            super(floor, Math::floor);
        }
    }

    /**
     * Ceils the provided model value using {@link Math#ceil(double)}.
     */
    public static class Ceil extends SingleOperandModelValueParams {
        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * @param ceil model value to ceil
         */
        @ConstructorProperties("ceil")
        public Ceil(ModelValueParams ceil) {
            super(ceil, Math::ceil);
        }
    }
}

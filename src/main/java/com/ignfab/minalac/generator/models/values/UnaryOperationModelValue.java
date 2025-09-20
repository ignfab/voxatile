package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.DoubleUnaryOperator;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value representing an operation on another model value.
 * The resulting value will be absent iff the operand value is.
 * @param operand the other model value
 * @param operator the operator to apply
 */
public record UnaryOperationModelValue(ModelValue operand, DoubleUnaryOperator operator) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return operand.get(model).map(operator::applyAsDouble);
    }
}

package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.DoubleBinaryOperator;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value representing an operation on two other model values.
 * The resulting value will be absent iff one of the operand value is.
 * @param leftOperand the first model value
 * @param rightOperand the second model value
 * @param operator the operator to apply
 */
public record BinaryOperationModelValue(ModelValue leftOperand, ModelValue rightOperand, DoubleBinaryOperator operator) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        Optional<Double> leftValue = leftOperand.get(model);
        Optional<Double> rightValue = rightOperand.get(model);
        if (leftValue.isEmpty() || rightValue.isEmpty())
            return Optional.empty();
        return Optional.of(operator.applyAsDouble(leftValue.get(), rightValue.get()));
    }
}

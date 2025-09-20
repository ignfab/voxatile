package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.DoubleBinaryOperator;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value representing an operation on two other model values.
 * The right value is not evaluated if the left value is absent.
 * The resulting value will be absent iff one of the operand value is.
 * @param leftOperand the first model value
 * @param rightOperand the second model value
 * @param operator the operator to apply
 */
public record BinaryOperationModelValue(ModelValue leftOperand, ModelValue rightOperand, DoubleBinaryOperator operator) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        Optional<Double> leftValue = leftOperand.get(model);
        if (leftValue.isPresent()) {
            Optional<Double> rightValue = rightOperand.get(model);
            if (rightValue.isPresent())
                return Optional.of(operator.applyAsDouble(leftValue.get(), rightValue.get()));
        }
        return Optional.empty();
    }
}

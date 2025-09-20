package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.DoubleBinaryOperator;

import com.ignfab.minalac.generator.models.Model;

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

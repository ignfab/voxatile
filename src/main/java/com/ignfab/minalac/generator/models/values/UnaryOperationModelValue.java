package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.DoubleUnaryOperator;

import com.ignfab.minalac.generator.models.Model;

public record UnaryOperationModelValue(ModelValue operand, DoubleUnaryOperator operator) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return operand.get(model).map(operator::applyAsDouble);
    }
}

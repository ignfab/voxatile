package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

public record FixedValue(double value) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return Optional.of(value);
    }
}

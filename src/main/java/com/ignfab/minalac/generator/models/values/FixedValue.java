package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value that always returns the specified value.
 * This value is never absent.
 * @param value the constant value
 */
public record FixedValue(double value) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return Optional.of(value);
    }
}

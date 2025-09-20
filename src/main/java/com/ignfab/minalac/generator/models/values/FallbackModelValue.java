package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

public record FallbackModelValue(ModelValue value, ModelValue fallback) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return value.get(model).or(() -> fallback.get(model));
    }
}

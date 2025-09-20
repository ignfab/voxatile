package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value that return another model value iff the original value is absent.
 * The fallback value is only evaluated if the original value is absent.
 * The resulting value will be absent iff both values are.
 * @param value the possibly absent value
 * @param fallback the other value to return if the first is absent
 */
public record FallbackModelValue(ModelValue value, ModelValue fallback) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return value.get(model).or(() -> fallback.get(model));
    }
}

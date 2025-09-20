package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value representing the mathematical inverse of another model value.
 * The resulting value will be absent iff the original value is.
 * If the value is equals to zero, it will be treated as absent as well.
 * @param value the other model value
 */
public record InverseModelValue(ModelValue value) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return value.get(model).map(x -> x == 0 ? null : 1 / x);
    }
}

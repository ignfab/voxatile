package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.ToDoubleFunction;

import com.ignfab.minalac.generator.models.Model;

// TODO generalize to all types of values?
public interface ModelValue extends ToDoubleFunction<Model> {
    Optional<Double> get(Model model);

    default Optional<Integer> getAsInt(Model model) {
        return get(model).map(Double::intValue);
    }

    @Override
    default double applyAsDouble(Model value) {
        return get(value).orElseThrow();
    }
}

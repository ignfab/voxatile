package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;

public record RandomUniformModelValue(ModelValue min, ModelValue max, Seed seed) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        Optional<Double> minValue = min.get(model);
        Optional<Double> maxValue = max.get(model);
        if (minValue.isEmpty() || maxValue.isEmpty())
            return Optional.empty();
        return Optional.of(seed.createRandom(model).nextDouble(minValue.get(), maxValue.get()));
    }
}

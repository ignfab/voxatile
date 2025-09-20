package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * A model value computing a random value following a uniform law between two bounds.
 * The resulting value will be absent iff one of the bound value is.
 * If the lower bound value is greater than or equals to the upper
 * bound value, it will be treated as absent as well.
 * @param min the lower bound of the value (inclusive)
 * @param max the upper bound of the value (exclusive)
 * @param seed the random seed
 */
public record RandomUniformModelValue(ModelValue min, ModelValue max, Seed seed) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        Optional<Double> minValue = min.get(model);
        Optional<Double> maxValue = max.get(model);
        if (minValue.isEmpty() || maxValue.isEmpty())
            return Optional.empty();
        double min = minValue.get();
        double max = maxValue.get();
        if (min >= max)
            return Optional.empty();
        return Optional.of(seed.createRandom(model).nextDouble(min, max));
    }
}

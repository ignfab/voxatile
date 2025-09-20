package com.ignfab.minalac.generator.models.values;

import java.util.Optional;
import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;

/**
 * Model value returning another model value based on a condition.
 * The resulting value will be absent iff the corresponding value is.
 * @param condition the predicate to decide which value to return
 * @param valueIfTrue the value to return for matching models
 * @param valueIfFalse the value to return for other models
 */
public record ConditionalModelValue(Predicate<Model> condition, ModelValue valueIfTrue, ModelValue valueIfFalse) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        return condition.test(model) ? valueIfTrue.get(model) : valueIfFalse.get(model);
    }
}

package com.ignfab.minalac.generator.models.filters;

import java.util.Optional;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * A model filter testing a model value using a given predicate.
 * An absent value is considered {@code false}.
 *
 * @param value model value to check
 * @param predicate condition to apply on the model value
 */
public record ModelFilterOnValue(ModelValue value, DoublePredicate predicate) implements Predicate<Model> {
    @Override
    public boolean test(Model model) {
        Optional<Double> v = value.get(model);
        return v.isPresent() && predicate.test(v.get());
    }
}

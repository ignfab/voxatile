package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

/**
 * Represents a numeric value that could be read from a {@link Model}.
 * The value might not exist for that model.
 * <p>
 * A model value can be seen as an operator producing a value from a model.
 */
// TODO generalize to all types of values
public interface ModelValue {
    /**
     * Returns the value from a specific model.
     * If the value does not exist, an {@link Optional#empty() empty optional} is returned instead.
     * @param model the model to use
     * @return the value as an optional double
     */
    Optional<Double> get(Model model);

    /**
     * Returns the value from a specific model as an integer.
     * If the value does not exist, an {@link Optional#empty() empty optional} is returned instead.
     * The decimal value is converted to an integer using {@link Double#intValue()}.
     * @param model the model to use
     * @return the value as an optional integer
     */
    default Optional<Integer> getAsInt(Model model) {
        return get(model).map(Double::intValue);
    }
}

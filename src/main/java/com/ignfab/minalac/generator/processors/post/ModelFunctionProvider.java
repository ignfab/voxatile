package com.ignfab.minalac.generator.processors.post;

import java.util.function.Function;

import com.ignfab.minalac.generator.models.Model;

/**
 * Provides a function from a model.
 * This is useful when function needs context from the model.
 *
 * @param <T> type of the function's return value
 */
@FunctionalInterface
public interface ModelFunctionProvider<T> {
    /**
     * {@return a function from a model}
     *
     * @param model the model from which the function is based.
     */
    Function<Object, ? extends T> function(Model model);
}

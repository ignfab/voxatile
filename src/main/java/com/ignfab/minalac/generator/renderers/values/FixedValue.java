package com.ignfab.minalac.generator.renderers.values;

import com.ignfab.minalac.generator.models.Model;

/**
 * A fixed model value, not depending on the model.
 *
 * @param <T> Type of the value
 */
public class FixedValue<T> implements ModelValue<T> {

    private T value;

    /**
     * Creates a new {@FixedValue}.
     *
     * @param value actual value
     */
    public FixedValue(T value) {
        this.value = value;
    }

    @Override
    public T get(Model model) {
        return value;
    }
}

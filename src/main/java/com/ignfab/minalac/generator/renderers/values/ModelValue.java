package com.ignfab.minalac.generator.renderers.values;

import com.ignfab.minalac.generator.models.Model;

/**
 * A value from a model.
 *
 * @param <T> type of the value
 */
public interface ModelValue<T> {

    /**
     * Get value from model.
     *
     * @param model model to get value from
     * @return the value
     */
    public T get(Model model);
}

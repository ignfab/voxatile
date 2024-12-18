package com.ignfab.minalac.generator.models;

import java.util.Iterator;
import java.util.function.Predicate;

import com.ignfab.minalac.generator.utils.iterator.Iterators;

/**
 * This class selects the models in the {@link ModelStore} matching a specified type.
 */
public class ModelSelection implements Iterable<Model> {
    private final ModelStore store;
    private final String type;
    private final Predicate<Model> filter;

    /**
     * Constructs a new {@code ModelSelection}.
     *
     * @param store the store containing the models
     * @param type the type of models to select
     * @param filter a filter on models to narrow down selection
     */
    public ModelSelection(ModelStore store, String type, Predicate<Model> filter) {
        this.store = store;
        this.type = type;
        this.filter = filter;
    }

    /**
     * Returns an iterator over the models matching the type of this {@code ModelSelection}.
     *
     * @return an iterator over the matching models
     */
    @Override
    public Iterator<Model> iterator() {
        if (filter == null)
            return store.getByType(type).iterator();

        return Iterators.filter(store.getByType(type).iterator(), filter);
    }
}

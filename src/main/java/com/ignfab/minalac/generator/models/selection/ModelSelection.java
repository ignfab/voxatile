package com.ignfab.minalac.generator.models.selection;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelStore;

import java.util.Iterator;

/**
 * This class selects the models in the {@link ModelStore} matching a specified type.
 */
public class ModelSelection implements ModelFilter {
    private final ModelStore store;
    private final String type;

    /**
     * Constructs a new {@code ModelSelection}.
     *
     * @param store the store containing the models
     * @param type the type of models to select
     */
    public ModelSelection(ModelStore store, String type) {
        this.store = store;
        this.type = type;
    }

    /**
     * Returns an iterator over the models matching the type of this {@code ModelSelection}.
     *
     * @return an iterator over the matching models
     */
    @Override
    public Iterator<Model> iterator() {
        return store.getByType(type).iterator();
    }
}

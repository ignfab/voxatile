package com.ignfab.minalac.generator.models;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.iterator.Iterables;

/**
 * This class selects the models in the {@link ModelStore} matching a specified type.
 */
public class ModelSelection {
    private final String type;
    private final Predicate<Model> filter;

    public static ModelSelection NONE = new None();

    /**
     * Constructs a new {@code ModelSelection}.
     *
     * @param type the type of models to select
     * @param filter a filter on models to narrow down selection
     */
    public ModelSelection(String type, Predicate<Model> filter) {
        this.type = type;
        this.filter = filter;
    }

    /**
     * Returns an iterable of the models matching the type of this {@code ModelSelection}.
     *
     * @param tile the tile to select models from
     * @return an iterable of the matching models
     */
    public Iterable<Model> forTile(GenerationTile tile) {
        List<Model> models = tile.models().getByType(type);
        return filter == null ? models : Iterables.filter(models, filter);
    }

    private static class None extends ModelSelection {
        public None() {
            super(null, null);
        }

        @Override
        public Iterable<Model> forTile(GenerationTile tile) {
            return () -> Collections.emptyIterator();
        }
    }
}

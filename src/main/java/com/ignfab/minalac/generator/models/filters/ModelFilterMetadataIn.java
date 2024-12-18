package com.ignfab.minalac.generator.models.filters;

import java.util.List;
import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model filter testing values of metadata.
 */
public class ModelFilterMetadataIn implements Predicate<Model> {

    private final String name;
    private final List<Object> values;

    /**
     * Creates a new {@code ModelFilterMetadataIn}.
     *
     * @param name name of metadata to test.
     * @param values list of possible values. If one corresponds to metadata value, the model is selected.
     */
    public ModelFilterMetadataIn(String name, List<Object> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public boolean test(Model model) {
        return values.contains(model.getMetadata(name));
    }
}

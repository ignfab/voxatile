package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value that returns the specified metadata value from the model.
 * If the model has no metadata with that name, the model value will be absent.
 * If the metadata value is not a number, it will be treated as absent as well.
 * @param name the name of the metadata value
 */
public record MetadataValue(String name) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        if (model.getMetadata(name) instanceof Number number)
            return Optional.of(number.doubleValue());
        return Optional.empty();
    }
}

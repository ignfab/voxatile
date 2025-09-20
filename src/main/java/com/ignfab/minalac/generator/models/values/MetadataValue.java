package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;

public record MetadataValue(String name) implements ModelValue {
    @Override
    public Optional<Double> get(Model model) {
        if (model.getMetadata(name) instanceof Number number)
            return Optional.of(number.doubleValue());
        return Optional.empty();
    }
}

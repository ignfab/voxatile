package com.ignfab.minalac.generator.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model filter testing metadata values using a given predicate.
 *
 * @param type expected metadata type
 * @param metadata metadata key to check
 * @param predicate condition to apply on the metadata value
 * @param <T> expected metadata type
 */
public record ModelFilterOnMetadataValue<T>(Class<T> type, String metadata, Predicate<T> predicate) implements Predicate<Model> {
    @Override
    public boolean test(Model model) {
        Object value = model.getMetadata(metadata);
        return type.isInstance(value) && predicate.test(type.cast(value));
    }
}

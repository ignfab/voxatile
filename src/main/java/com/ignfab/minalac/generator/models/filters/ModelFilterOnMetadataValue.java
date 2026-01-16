package com.ignfab.minalac.generator.models.filters;

import java.text.NumberFormat;
import java.text.ParseException;
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

        // Shortcut for String values convertible to number values
        if (!type.isInstance(value)) {
            if (type == Number.class && value instanceof String)
                try {
                    Number number = NumberFormat.getInstance().parse((String)value);
                    return predicate.test(type.cast(number));
                } catch (ParseException e) {}
            return false;
        }

        return predicate.test(type.cast(value));
    }
}

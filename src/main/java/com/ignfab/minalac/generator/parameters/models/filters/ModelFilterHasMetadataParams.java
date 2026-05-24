package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;

/**
 * Parameters for a {@link ModelFilterHasMetadata}.
 */
public class ModelFilterHasMetadataParams extends ModelFilterParams {
    /**
     * List of metadata names to check (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<String> hasMetadata;

    /**
     * Creates a new {@code ModelFilterHasMetadataParams}.
     *
     * @param hasMetadata list of metadata names to check.
     */
    @ConstructorProperties({"hasMetadata"})
    public ModelFilterHasMetadataParams(List<String> hasMetadata) {
        this.hasMetadata = hasMetadata;
    }

    @Override
    public void validate() {
        if (hasMetadata.isEmpty())
            throw new IllegalArgumentException("There must be at least one metadata name");

        for (String name : hasMetadata)
            if (name.isBlank())
                throw new IllegalArgumentException("Metadata name cannot be empty or blank");
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        Iterator<String> iterator = hasMetadata.iterator();
        if (!iterator.hasNext())
            throw new IllegalArgumentException("There must be at least one metadata name (should have been tested with validation!)");

        Predicate<Model> predicate = new ModelFilterHasMetadata(iterator.next());

        while (iterator.hasNext())
            predicate = predicate.and(new ModelFilterHasMetadata(iterator.next()));

        return predicate;
    }
}

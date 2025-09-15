package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Parameters for a {@link ModelFilterHasMetadata}.
 */
public class ModelFilterHasMetadataParams extends ModelFilterParams {
    /**
     * List of metadata names to check (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<StringNotBlank> hasMetadata;

    /**
     * Creates a new {@code ModelFilterHasMetadataParams}.
     *
     * @param hasMetadata list of metadata names to check.
     */
    @ConstructorProperties({"hasMetadata"})
    public ModelFilterHasMetadataParams(List<StringNotBlank> hasMetadata) {
        this.hasMetadata = hasMetadata;
    }

    @Override
    public void validate() {
        if (hasMetadata.isEmpty())
            throw new IllegalArgumentException("There must be at least one metadata name");
    }

    @Override
    public Predicate<Model> create() {
        Iterator<StringNotBlank> iterator = hasMetadata.iterator();
        if (!iterator.hasNext())
            throw new IllegalArgumentException("There must be at least one metadata name (should have been tested with validation!)");

        Predicate<Model> predicate = new ModelFilterHasMetadata(iterator.next().create());

        while (iterator.hasNext())
            predicate = predicate.and(new ModelFilterHasMetadata(iterator.next().create()));

        return predicate;
    }
}

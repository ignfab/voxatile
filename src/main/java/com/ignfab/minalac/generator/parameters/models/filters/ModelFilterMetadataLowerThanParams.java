package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterMetadataLowerThan;

/**
 * Parameters for a filter that selects models where the metadata value is less than a specified threshold.
 */
public class ModelFilterMetadataLowerThanParams extends ModelFilterParams {

    /**
     * Name of the metadata to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Number that the metadata must be less than (required).
     */
    // TODO: Support all Comparable types, not just Number
    @JsonSetter(nulls = Nulls.FAIL)
    public double lowerThan;

    /**
     * Creates a new {@code ModelFilterMetadataLowerThanParams}.
     *
     * @param metadata name of the metadata to test
     * @param lowerThan number that the metadata must be less than
     */
    @ConstructorProperties({"metadata", "lowerThan"})
    public ModelFilterMetadataLowerThanParams(String metadata, double lowerThan) {
        this.metadata = metadata;
        this.lowerThan = lowerThan;
    }

    @Override
    public void validate() {
        if (metadata.isBlank())
            throw new IllegalArgumentException("Metadata name cannot be empty or blank");
    }

    @Override
    public Predicate<Model> create() {
        return new ModelFilterMetadataLowerThan(metadata, lowerThan);
    }
}

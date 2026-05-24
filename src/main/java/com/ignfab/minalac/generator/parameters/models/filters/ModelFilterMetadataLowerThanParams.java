package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;

/**
 * Parameters for a filter that selects models where the metadata value is less than a specified threshold.
 */
public class ModelFilterMetadataLowerThanParams extends ModelFilterParams {

    /**
     * Name of the metadata to compare (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Threshold value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double lowerThan;

    /**
     * Creates a new {@code ModelFilterMetadataLowerThanParams}.
     *
     * @param metadata name of the metadata to compare
     * @param lowerThan threshold value
     */
    @ConstructorProperties({ "metadata", "lowerThan" })
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
    public Predicate<Model> create(Generation generation) {
        return new ModelFilterOnMetadataValue<>(Number.class, metadata, metadataValue -> metadataValue.doubleValue() < lowerThan);
    }
}

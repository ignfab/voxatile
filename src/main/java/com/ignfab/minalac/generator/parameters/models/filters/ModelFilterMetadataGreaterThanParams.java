package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;

/**
 * Parameters for a filter that selects models where the metadata value is greater than a specified threshold.
 */
public class ModelFilterMetadataGreaterThanParams extends ModelFilterParams {

    /**
     * Name of the metadata to compare (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Threshold value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double greaterThan;

    /**
     * Creates a new {@code ModelFilterMetadataGreaterThanParams}.
     *
     * @param metadata name of the metadata to compare
     * @param greaterThan threshold value
     */
    @ConstructorProperties({ "metadata", "greaterThan" })
    public ModelFilterMetadataGreaterThanParams(String metadata, double greaterThan) {
        this.metadata = metadata;
        this.greaterThan = greaterThan;
    }

    @Override
    public void validate() {
        if (metadata.isBlank())
            throw new IllegalArgumentException("Metadata name cannot be empty or blank");
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return new ModelFilterOnMetadataValue<>(Number.class, metadata, metadataValue -> metadataValue.doubleValue() > greaterThan);
    }
}

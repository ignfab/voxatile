package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Parameters for a filter that selects models where the metadata value is greater than a specified threshold.
 */
public class ModelFilterMetadataGreaterThanParams extends ModelFilterParams {

    /**
     * Name of the metadata to compare (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank metadata;

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
    public ModelFilterMetadataGreaterThanParams(StringNotBlank metadata, double greaterThan) {
        this.metadata = metadata;
        this.greaterThan = greaterThan;
    }

    @Override
    public Predicate<Model> create() {
        return new ModelFilterOnMetadataValue<>(Number.class, metadata.create(), metadataValue -> metadataValue.doubleValue() > greaterThan);
    }
}

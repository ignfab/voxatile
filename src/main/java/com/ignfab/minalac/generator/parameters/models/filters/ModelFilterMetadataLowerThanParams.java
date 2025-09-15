package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Parameters for a filter that selects models where the metadata value is less than a specified threshold.
 */
public class ModelFilterMetadataLowerThanParams extends ModelFilterParams {

    /**
     * Name of the metadata to compare (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank metadata;

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
    public ModelFilterMetadataLowerThanParams(StringNotBlank metadata, double lowerThan) {
        this.metadata = metadata;
        this.lowerThan = lowerThan;
    }

    @Override
    public Predicate<Model> create() {
        return new ModelFilterOnMetadataValue<>(Number.class, metadata.create(), metadataValue -> metadataValue.doubleValue() < lowerThan);
    }
}

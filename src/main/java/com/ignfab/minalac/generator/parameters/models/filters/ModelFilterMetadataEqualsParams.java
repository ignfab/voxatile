package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;
import com.ignfab.minalac.generator.parameters.ValueParser;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Parameters for an "equals" operator.
 */
public class ModelFilterMetadataEqualsParams extends ModelFilterParams {

    /**
     * Name of the metadata to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank metadata;

    /**
     * Value of metadata to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public Object equals;

    /**
     * Type of that value (optional, default "string").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ValueParser<?> as = ValueParser.STRING;

    /**
     * Creates a new {@code ModelFilterMetadataEqualsParams}.
     *
     * @param metadata name of the metadata to test.
     * @param equals value of metadata to test.
     */
    @ConstructorProperties({"metadata", "equals"})
    public ModelFilterMetadataEqualsParams(StringNotBlank metadata, Object equals) {
        this.metadata = metadata;
        this.equals = equals;
    }

    @Override
    public Predicate<Model> create() {
        Object equals = as.parse(this.equals);
        return new ModelFilterOnMetadataValue<>(as.type(), metadata.create(), equals::equals);
    }
}

package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;
import com.ignfab.minalac.generator.parameters.ValueParser;

/**
 * Parameters for filtering models by metadata values.
 */
public class ModelFilterMetadataInParams extends ModelFilterParams {

    /**
     * Name of the metadata to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * List of possible values for that metadata (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<Object> in;

    /**
     * Type of these values (optional, default "string").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ValueParser<?> as = ValueParser.STRING;

    /**
     * Creates a new {@code ModelFilterMetadataInParams}.
     *
     * @param metadata name of the metadata to test.
     * @param in list of possible values for that metadata.
     */
    @ConstructorProperties({"metadata", "in"})
    public ModelFilterMetadataInParams(String metadata, List<Object> in) {
        this.metadata = metadata;
        this.in = in;
    }

    @Override
    public void validate() {
        if (metadata.isBlank())
            throw new IllegalArgumentException("Metadata name cannot be empty or blank");
        if (in.isEmpty())
            throw new IllegalArgumentException("Values list cannot not be empty");
    }

    @Override
    public Predicate<Model> create() {
        List<? extends Object> inParsed = in.stream().map(as::parse).toList();
        return new ModelFilterOnMetadataValue<>(as.type(), metadata, inParsed::contains);
    }
}

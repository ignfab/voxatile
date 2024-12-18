package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ignfab.minalac.generator.models.Model;

/**
 * Abstract parameter class for all model filters parameters.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(ModelFilterNotParams.class),
    @JsonSubTypes.Type(ModelFilterAndParams.class),
    @JsonSubTypes.Type(ModelFilterOrParams.class),
    @JsonSubTypes.Type(ModelFilterMetadataEqualsParams.class),
    @JsonSubTypes.Type(ModelFilterMetadataInParams.class),
    @JsonSubTypes.Type(ModelFilterHasMetadataParams.class)
})
public abstract class ModelFilterParams {
    /**
     * Validates params.
     */
    public void validate() {}

    /**
     * Creates a {@code Predicate<Model>} out of these params.
     *
     * @return the resulting predicate.
     */
    public abstract Predicate<Model> create();
}

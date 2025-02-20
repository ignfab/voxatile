package com.ignfab.minalac.generator.parameters.models;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterParams;

/**
 * Parameters for a {@link ModelSelection}.
 */
public class ModelSelectionParams {
    /**
     * Type of model (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String type;

    /**
     * Extra filter (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelFilterParams filter = null;

    /**
     * Creates a new {@code ModelFilterParams}.
     *
     * @param type Type of model to select.
     */
    @ConstructorProperties({"type"})
    public ModelSelectionParams(String type) {
        this.type = type;
    }

    /**
     * Validates params.
     */
    public void validate() {
        if (type.isBlank())
            throw new IllegalArgumentException("Model type cannot be empty or blank");
        if (filter != null)
            filter.validate();
    }

    /**
     * Creates a new {@link ModelSelection} out of params.
     *
     * @param store Model store which select models from.
     *
     * @return a model selection.
     */
    public ModelSelection create(ModelStore store) {
        return new ModelSelection(store, type, (filter == null) ? null : filter.create());
    }
}

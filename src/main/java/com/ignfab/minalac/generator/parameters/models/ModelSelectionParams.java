package com.ignfab.minalac.generator.parameters.models;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterParams;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Parameters for a {@link ModelSelection}.
 */
public class ModelSelectionParams {
    /**
     * Type of model (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank type;

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
    public ModelSelectionParams(StringNotBlank type) {
        this.type = type;
    }

    /**
     * Creates a new {@link ModelSelection} out of params.
     *
     * @return a model selection.
     */
    public ModelSelection create() {
        return new ModelSelection(type.create(), (filter == null) ? null : filter.create());
    }
}

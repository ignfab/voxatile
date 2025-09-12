package com.ignfab.minalac.generator.parameters.models;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterAndParams;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterParams;

/**
 * Parameters for a {@link ModelSelection}.
 */
public class ModelSelectionParams {
    /**
     * Type of model (required).
     */
    @JsonSetter(nulls = Nulls.SKIP)
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

    private boolean isNone = false;

    public void narrowDown(ModelSelectionParams params) {
        if (params.isNone || type != null && params.type != null && !type.equals(params.type)) {
            // If we have two different types, no model will ever match
            isNone = true;
            return;
        }

        if (type == null)
            type = params.type;

        if (params.filter != null)
            filter = filter == null ? params.filter : new ModelFilterAndParams(List.of(filter, params.filter));
    }

    /**
     * Validates params.
     */
    public void validate() {
        if (isNone)
            return;
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Model type cannot be empty or blank");
        if (filter != null)
            filter.validate();
    }

    /**
     * Creates a new {@link ModelSelection} out of params.
     *
     * @return a model selection.
     */
    public ModelSelection create() {
        if (isNone)
            return ModelSelection.NONE;

        return new ModelSelection(type, (filter == null) ? null : filter.create());
    }
}

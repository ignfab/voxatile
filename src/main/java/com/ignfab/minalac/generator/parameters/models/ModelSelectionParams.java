package com.ignfab.minalac.generator.parameters.models;

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

    private boolean isNone = false;

    /**
     * Narrows down this selection params accorging to given selection params will select only models also fitting other selection params.
     * <p>
     * This may lead to empty selection, in particular if other selection params has a different model type than this.
     *
     * @param params other selection params
     */
    public void narrowDown(ModelSelectionParams params) {
        if (isNone)
            return;

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
        if (type != null && type.isBlank())
            throw new IllegalArgumentException("Model type cannot be blank");
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

        // Type presence cannot be checked at validation because it could be given later (selection narrowing)
        if (type == null)
            throw new IllegalArgumentException("Model selection must have a model type");

        return new ModelSelection(type, (filter == null) ? null : filter.create());
    }
}

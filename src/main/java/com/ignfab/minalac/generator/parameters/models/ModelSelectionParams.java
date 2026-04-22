package com.ignfab.minalac.generator.parameters.models;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterParams;

/**
 * Parameters for a {@link ModelSelection}.
 */
public class ModelSelectionParams {
    /**
     * Type of model (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String type;

    /**
     * Extra filter (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelFilterParams filter = null;

    /**
     * Narrows this selection params down according to the given selection params
     * This will select only models also fitting other selection params.
     * <p>
     * This may lead to empty selection, in particular if other selection params has a different model type than this.
     *
     * @param params other selection params
     */
    /*public void narrowDown(ModelSelectionParams params) {
        if (type != null && params.type != null && !type.equals(params.type))
            throw new IllegalArgumentException("Model selection cannot have two different model types (it would select nothing)");

        if (type == null)
            type = params.type;

        if (params.filter != null)
            filter = filter == null ? params.filter : new ModelFilterAndParams(List.of(filter, params.filter));
    }*/

    public ModelSelectionParams inheriting(ModelSelectionParams inherited) {
        if (type != null && inherited.type != null && !type.equals(inherited.type))
            throw new IllegalArgumentException("Model selection cannot have two different model types (it would select nothing)");

        ModelSelectionParams result = new ModelSelectionParams();
        result.type = type == null ? inherited.type : type;
        result.filter = ModelFilterParams.and(filter, inherited.filter);
        return result;
    }

    /**
     * Validates params.
     */
    public void validate() {
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
        // Type presence cannot be checked at validation because it could be given later (selection narrowing)
        if (type == null)
            throw new IllegalArgumentException("Model selection must have a model type");

        return new ModelSelection(type, (filter == null) ? null : filter.create());
    }
}

package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;

/**
 * Abstract class for {@link TaskParams} having a model selection.
 */
public abstract class ModelTaskParams extends TaskParams {
    /**
     * The type of models to get data from (optional).
     *
     * This field is optional but before validation, this task must have
     * a non null valid model selection (it could get it from composite tasks like sequences).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models = new ModelSelectionParams();

    @Override
    public void validate() {
        super.validate();
        models.validate();
    }
}

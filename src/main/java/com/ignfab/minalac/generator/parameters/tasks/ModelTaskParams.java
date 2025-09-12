package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;

/**
 * Abstract class for {@link TaskParams} having a model selection modifiable by {@link TileSequenceParams}.
 */
public abstract class ModelTaskParams extends TileTaskParams {
    /**
     * The type of models to get data from (optional).
     *
     * This field is optional but before validation, this task must have
     * a non null valid model selection (it could get it from composite tasks like sequences).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models;

    @Override
    public void validate() {
        if (models == null)
            throw new IllegalArgumentException("Missing \"models\" field");
        models.validate();
    }
}

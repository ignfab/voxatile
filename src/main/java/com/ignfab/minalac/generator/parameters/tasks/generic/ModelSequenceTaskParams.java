package com.ignfab.minalac.generator.parameters.tasks.generic;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.HasModelSelection;

public class ModelSequenceTaskParams<T> extends SequenceTaskParams<T> implements HasModelSelection {

    @ConstructorProperties("tasks")
    public ModelSequenceTaskParams(List<TaskParams<T>> tasks) {
        super(tasks);
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models = new ModelSelectionParams();

    @Override
    public ModelSelectionParams models() {
        return models;
    }

}

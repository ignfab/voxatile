package com.ignfab.minalac.generator.parameters.tasks.generic;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.HasModelSelection;

public class ModelScheduleTaskParams<T> extends ScheduleTaskParams<T> implements HasModelSelection {

    @ConstructorProperties("tasks")
    public ModelScheduleTaskParams(NamedTaskListParams<T> tasks) {
        super(tasks);
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models = new ModelSelectionParams();

    @Override
    public ModelSelectionParams models() {
        return models;
    }
}

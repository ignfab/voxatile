package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.HasModelSelection;
import com.ignfab.minalac.generator.parameters.tasks.generic.NamedTaskListParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.ScheduleTaskParams;

public class TileScheduleTaskParams extends ScheduleTaskParams<GenerationTile> implements HasModelSelection {

    @ConstructorProperties("tasks")
    public TileScheduleTaskParams(NamedTaskListParams<GenerationTile> tasks) {
        super(tasks);
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models = new ModelSelectionParams();

    @Override
    public ModelSelectionParams models() {
        return models;
    }
}

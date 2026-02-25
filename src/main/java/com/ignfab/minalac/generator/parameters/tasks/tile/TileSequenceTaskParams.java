package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.HasModelSelection;
import com.ignfab.minalac.generator.parameters.tasks.generic.SequenceTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.TaskParams;

public class TileSequenceTaskParams extends SequenceTaskParams<GenerationTile> implements HasModelSelection {

    @ConstructorProperties("tasks")
    public TileSequenceTaskParams(List<TaskParams<GenerationTile>> tasks) {
        super(tasks);
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models = new ModelSelectionParams();

    @Override
    public ModelSelectionParams models() {
        return models;
    }

}

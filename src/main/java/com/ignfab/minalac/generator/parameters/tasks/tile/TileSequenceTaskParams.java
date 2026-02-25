package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<String, TaskParams<GenerationTile>> flatten(String mainName) {
        Map<String, TaskParams<GenerationTile>> result = super.flatten(mainName);

        // Merge model selections
        for (TaskParams<GenerationTile> task : result.values())
            if (task instanceof ModelTaskParams modelTask)
                modelTask.models().narrowDown(models());

        return result;
    }
}

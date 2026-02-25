package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.ScheduleTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.TaskParams;

/**
 * Parameters for a task running other tasks, parallelized, with dependencies between them (like in a schedule).
 * <p>
 * Same as {@link ScheduleTaskParams} but for tile tasks, and with a model selection management.
 */
public class TileScheduleTaskParams extends ScheduleTaskParams<GenerationTile> implements HasModelSelection {
    /**
     * Models to get data from (optional).
     *
     * If given, this model selection will be inherited (and may be narrowed down) by subtasks.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelSelectionParams models = new ModelSelectionParams();

    /**
     * Creates a new {@link TileScheduleTaskParams}.
     *
     * @param tasks subtasks indexed by their name
     */
    @ConstructorProperties("tasks")
    public TileScheduleTaskParams(Map<String, TaskParams<GenerationTile>> tasks) {
        super(tasks);
    }

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

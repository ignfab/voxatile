package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.tasks.TileTaskSequence;

/**
 * Parameters for a {@link TileTaskSequence}.
 */
public class TileTaskSequenceParams extends TileTaskParams {

    /**
     * Tasks list (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<TileTaskParams> tasks;

    /**
     * Model selection (optional).
     *
     * @see #setModels()
     */
    private ModelSelectionParams models;

    /**
     * Creates a new {@code TileTaskSequenceParams} with required values.
     *
     * @param tasks tasks list
     */
    @ConstructorProperties({"tasks"})
    public TileTaskSequenceParams(List<TileTaskParams> tasks) {
        this.tasks = tasks;
    }

    /**
     * Set model selection for the whole sequence and narrow down tasks model selections with it.
     *
     * @param models model selection
     */
    @JsonSetter(value = "models", nulls = Nulls.SKIP)
    public void setModels(ModelSelectionParams models) {
        if (this.models != null)
            throw new IllegalArgumentException("Model selection cannot be set twice");

        this.models = models;

        if (models != null)
            for (TileTaskParams task : tasks)
                if (task instanceof ModelTaskParams modelTask)
                    if (modelTask.models == null)
                        modelTask.models = models;
                    else
                        modelTask.models.narrowDown(models);
    }

    @Override
    public void validate() {
        for (TileTaskParams task : tasks) {
            if (!task.after.isEmpty())
                throw new IllegalArgumentException("In a sequence, tasks cannot have dependancies");

            task.validate();
        }
    }

    @Override
    public TileTask create(Generation generation) {
        TileTaskSequence sequence = new TileTaskSequence();
        for (TileTaskParams task : tasks)
            sequence.add(task.create(generation));

        return sequence;
    }
}

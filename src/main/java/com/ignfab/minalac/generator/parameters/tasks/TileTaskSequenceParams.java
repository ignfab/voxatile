package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.tasks.TileTaskSequence;

public class TileTaskSequenceParams extends TileTaskParams {
    /**
     * Tasks list (required)
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<TileTaskParams> tasks;

    @ConstructorProperties({"tasks"})
    public TileTaskSequenceParams(List<TileTaskParams> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        for (TileTaskParams task: tasks) {
            if (!task.after.isEmpty())
                throw new IllegalArgumentException("In a sequence, tasks cannot have dependancies");

            task.validate();
        }
    }

    @Override
    public TileTask create(Generation generation) {
        TileTaskSequence sequence = new TileTaskSequence();
        for (TileTaskParams task: tasks)
            sequence.add(task.create(generation));

        return sequence;
    }
}
package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.tasks.Task;

/**
 * Parameters for a task running other tasks one by one in sequence.
 * <p>
 * This params class only instantiates a {@link NoOperationTask} but it
 * {@link #createAdditionalTaskParams creates additional task params} for its subtasks
 */
public class SequenceTaskParams extends ModelTaskParams {

    /**
     * List of subtasks in run order.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public List<TaskParams> subtasks;

    /**
     * List of imported dependencies (names of external tasks that could be used as dependencies for subtasks).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> using = new HashSet<>();

    /**
     * Creates a new {@code SequenceTaskParams} with required fields.
     *
     * @param subtasks list of subtasks in run order
     */
    @ConstructorProperties("subtasks")
    public SequenceTaskParams(List<TaskParams> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public void validate() {
        super.validate();

        using.forEach(TaskParams::validateTaskName);
        subtasks.forEach(TaskParams::validate);
    }

    @Override
    public Map<String, TaskParams> createAdditionalTaskParams(String prefix) {
        Map<String, TaskParams> result = new HashMap<>();

        int index = 0;
        for (TaskParams subtask : subtasks) {
            if (index == 0)
                // First task starts after sequence afters
                subtask.after.addAll(after);
            else
                // Other tasks start after their previous task
                subtask.after.add(prefix + SEPARATOR + index);

            index++;

            String subname = prefix + SEPARATOR + index;

            result.put(subname, subtask);

            // getAditionalTaskParams is supposed to prefix all its results with subname
            // so we can presume no result will be overwritten.
            result.putAll(subtask.createAdditionalTaskParams(subname));
        }

        // Merge model selections
        for (TaskParams task : result.values())
            if (task instanceof ModelTaskParams modelTask)
                modelTask.models.narrowDown(models);

        // Sequence task (which is a noop marker) starts after last task
        after = Set.of(prefix + SEPARATOR + index);

        return result;
    }

    @Override
    public Task create(Generation generation) {
        return NoOperationTask.instance();
    }

}

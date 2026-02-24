package com.ignfab.minalac.generator.parameters.tasks.generic;

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
import com.ignfab.minalac.generator.parameters.tasks.HasModelSelection;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a task running other tasks one by one in sequence.
 * <p>
 * This params class only instantiates a {@link NoOperationTask} but it
 * {@link #createAdditionalTaskParams creates additional task params} for its subtasks
 */
public class SequenceTaskParams<T> extends TaskParams<T> {
    /**
     * List of subtasks in run order.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public List<TaskParams<T>> tasks;

    /**
     * List of imported dependencies (names of external tasks that could be used as dependencies for subtasks).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> using = new HashSet<>();

    /**
     * Creates a new {@code SequenceTaskParams} with required fields.
     *
     * @param tasks list of subtasks in run order
     */
    @ConstructorProperties("tasks")
    public SequenceTaskParams(List<TaskParams<T>> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();

        using.forEach(TaskParams::validateName);
        tasks.forEach(TaskParams::validate);
    }

    @Override
    public Map<String, TaskParams<T>> flatten(String name) {
        Map<String, TaskParams<T>> result = new HashMap<>();

        int index = 0;
        for (TaskParams<T> task : tasks) {
            if (index == 0)
                // First task starts after sequence afters
                task.after.addAll(after);
            else
                // Other tasks start after their previous task
                task.after.add(name + SEPARATOR + index);

            index++;

            String taskName = name + SEPARATOR + index;

            result.putAll(task.flatten(taskName));
        }
/* TODO LATER
        // Merge model selections
        if (this instanceof HasModelSelection modelThis)
            for (TaskParams<T> task : result.values())
                if (task instanceof HasModelSelection modelTask)
                    modelTask.models().narrowDown(modelThis.models());
*/
        // Sequence task (which is a noop marker) starts after last task
        TaskParams<T> endTask = new NoOperationTaskParams<>();
        endTask.after = Set.of(name + SEPARATOR + index);
        result.put(name, endTask);

        return result;
    }

    @Override
    public Task<T> create(Generation generation) {
                //TODO: Should throw exception

        return NoOperationTask.instance();
    }
}


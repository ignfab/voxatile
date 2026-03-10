package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;

/**
 * Parameters for a task running other tasks one by one in sequence.
 * <p>
 * This params class is not intended to instantiate a task object using {@link #create(Generation)}.
 * Subtasks params and a {@link NoOperationTaskParams} (end task) will rather be created using {@link #flatten(String)}.
 * Then, these flattened task params can be used to instantiate corresponding task objects.
 */
public class SequenceTaskParams extends CompositeTaskParams {
    /**
     * List of subtasks in run order.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public List<TaskParams> tasks;

    /**
     * Creates a new {@code SequenceTaskParams} with required fields.
     *
     * @param tasks list of subtasks in run order
     */
    @ConstructorProperties("tasks")
    public SequenceTaskParams(List<TaskParams> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();

        for (TaskParams subtask : tasks) {
            for (String after : subtask.after)
                if (!using.contains(after))
                    throw new IllegalArgumentException("Unknown task \"%s\" in after list".formatted(after));

            subtask.validate();
        }
    }

    @Override
    public Map<String, TaskParams> flatten(String parentName) {
        // Resulting task params indexed by name
        Map<String, TaskParams> result = new HashMap<>();

        // Flatten subtasks and perform translations (and naming)
        int index = 0;

        for (TaskParams task : tasks) {
            // Add dependencies between each sequence task
            if (index > 0)
                task.after.add(makeTaskName(index));

            index++;
            populate(result, parentName, makeTaskName(index), task);
        }

        inheritAfters(result);

        // Sequence task (which is a noop task) starts after last task
        TaskParams endTask = new NoOperationTaskParams();
        endTask.after = Set.of(makeTaskFullName(parentName, makeTaskName(index)));
        result.put(parentName, endTask);

        return result;
    }
}

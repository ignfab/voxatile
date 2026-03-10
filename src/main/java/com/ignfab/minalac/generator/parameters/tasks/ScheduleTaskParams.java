package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;

/**
 * Parameters for a task running other tasks, parallelized, with dependencies between them (like in a schedule).
 * <p>
 * This params class is not intended to instantiate a task object using {@link #create(Generation)}.
 * Subtasks params and a {@link NoOperationTaskParams} (end task) will rather be created using {@link #flatten(String)}.
 * Then, these flattened task params can be used to instantiate corresponding task objects.
 */
public class ScheduleTaskParams extends CompositeTaskParams {

    /**
     * List of subtasks indexed by their names.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public Map<String, TaskParams> tasks;

    /**
     * Creates a new {@code ScheduleTaskParams} with required fields.
     *
     * @param tasks Subtasks indexed names.
     */
    @ConstructorProperties("tasks")
    public ScheduleTaskParams(Map<String, TaskParams> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();

        tasks.forEach((name, subtask) -> {
            validateTaskName(name);
            if (using.contains(name))
                throw new IllegalArgumentException(
                    "Cannot use \"%s\" as task name as it is already in using list"
                    .formatted(name)
                );

            for (String after : subtask.after)
                // Task may depend on a task at same level or listed in using list.
                if (!using.contains(after) && !tasks.containsKey(after))
                    throw new IllegalArgumentException("Unknown task \"%s\" in after list".formatted(after));

            subtask.validate();
        });
    }

    @Override
    public Map<String, TaskParams> flatten(String parentName) {

        // Resulting task params indexed by name
        Map<String, TaskParams> result = new LinkedHashMap<>();

        // Subtasks known to be followed by another subtask
        Set<String> followed = tasks.values().stream()
            .flatMap((task) -> task.after.stream())
            .filter(Predicate.not(using::contains))
            .collect(Collectors.toSet());

        // Flatten subtasks and perform translations
        tasks.forEach((name, task) -> populate(result, parentName, name, task));

        inheritAfters(result);

        // Replace end task dependencies with all non followed subtasks.
        // End task is a NoOperation task that will start after all subtasks have ended
        // so it can be safely used as "after" for "after all subtasks ended".
        TaskParams endTask = new NoOperationTaskParams();
        endTask.after = tasks.keySet().stream()
            .filter(Predicate.not(followed::contains))
            .map(name -> makeTaskFullName(parentName, name))
            .collect(Collectors.toSet());

        result.put(parentName, endTask);

        return result;
    }
}

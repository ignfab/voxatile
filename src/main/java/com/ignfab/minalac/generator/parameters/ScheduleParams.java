package com.ignfab.minalac.generator.parameters;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.tasks.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Schedule;

/**
 * Parameters for a tile task schedule.
 */
@JsonWrapper
public class ScheduleParams {

    /**
     * Tasks, indexed by name, in this schedule.
     */
    @JsonSetter(contentNulls = Nulls.FAIL)
    public Map<String, TaskParams> tasks = new LinkedHashMap<>();

    /**
     * Flattens schedule params.
     * <p>
     * Schedule params may have composite tasks with subtasks. After `flatten`, it will only contain tasks at the main level,
     * with translated names and dependencies, so it works exactly the same. This step is required in order to be able to create actual schedule.
     */
    public void flatten() {
        Map<String, TaskParams> flat = new LinkedHashMap<>();
        tasks.forEach((name, task) -> flat.putAll(task.flatten(name)));
        tasks = flat;
    }

    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if schedule parameter is invalid.
     */
    public void validate() {
        tasks.keySet().forEach(TaskParams::validateTaskName);
        tasks.values().forEach(TaskParams::validate);
    }

    /**
     * Populates an existing {@link Schedule} with tasks from this {@link ScheduleParams}.
     *
     * @param generation Generation context object
     * @param schedule {@link Schedule} to populate
     */
    public void populate(Generation generation, Schedule schedule) {
        // Flatten schedule
        flatten();

        // Create actual tasks
        tasks.forEach((name, params) -> schedule.addTask(name, params.create(generation)));

        // Once all tasks created, we can add dependencies
        tasks.forEach((name, params) -> {
            for (String after : params.after)
                schedule.addDependency(name, after);
        });
    }
}

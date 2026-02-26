package com.ignfab.minalac.generator.parameters;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.tasks.generic.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Scheduler;

/**
 * Params for a {@link Scheduler<T>}.
 *
 * @param <T> Type of scheduler tasks execution context type (same as in {@link Scheduler<T>}).
 */
public class ScheduleParams<T> {
    @JsonIgnore // Or task named "tasks" will not call setter (put)
    private HashMap<String, TaskParams<T>> tasks = new HashMap<>();

    /**
     * Puts a new task in list.
     * <p>
     * This is the only, and default, setter for all keys.
     * This is a trick to makes {@code ScheduleParams<T>} behave like a {@code Map<String, TaskParams<T>>}.
     *
     * @param name Name of the task to add
     * @param task Parameters of task to add
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonAnySetter
    public void put(String name, TaskParams<T> task) {
        TaskParams.validateName(name);

        // This should not happen in Jackson context as duplicate keys are invalid in Yaml.
        // But this method could be used outside that context.
        if (tasks.containsKey(name))
            throw new IllegalArgumentException("Duplicate task name \"%s\".".formatted(name));

        tasks.put(name, task);
    }

    /**
     * Gets a tasks from schedule.
     *
     * @param name Name of the task to add
     * @return Found task or null if no task found
     */
    public TaskParams<T> get(String name) {
        return tasks.get(name);
    }

    /**
     * Validates this schedule and all its subtasks.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    public void validate() {
        tasks.values().forEach(TaskParams::validate);
    }

    /**
     * Creates corresponding {@link Scheduler<T>} object.
     *
     * @param generation Generation for which create the scheduler
     *
     * @return created scheduler
     */
    public Scheduler<T> create(Generation generation) {
        Scheduler<T> scheduler = new Scheduler<>();

        Map<String, TaskParams<T>> flat = new HashMap<>();
        tasks.forEach((name, task) -> flat.putAll(task.flatten(name)));

        // Create tasks
        flat.forEach((name, task) -> scheduler.schedule(name, task.create(generation)));

        // Once all tasks created, we can add dependencies
        flat.forEach((name, task) -> {
            for (String after : task.after)
                scheduler.addDependency(name, after);
        });

        return scheduler;
    }
}

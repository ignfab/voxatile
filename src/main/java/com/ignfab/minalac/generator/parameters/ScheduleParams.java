package com.ignfab.minalac.generator.parameters;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.tasks.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Schedule;
import com.ignfab.minalac.generator.utils.execution.Scheduler;

/**
 * Parameters for a tile task schedule.
 */
public class ScheduleParams {

    /**
     * Tasks, indexed by name, in this schedule.
     * <p>
     * This field is not deserialized, we use `@JsonAnySetter` for the whole deserialization.
     * It is package public for test purposes.
     */
    @JsonIgnore // Or task named "tasks" will not call setter (put)
    /*package public*/ Map<String, TaskParams> tasks = new HashMap<>();

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
    public void put(String name, TaskParams task) {
        TaskParams.validateTaskName(name);

        // This should not happen in Jackson context as duplicate keys are invalid in Yaml.
        // But this method could be used outside that context.
        if (tasks.containsKey(name))
            throw new IllegalArgumentException("Duplicate task name \"%s\".".formatted(name));

        tasks.put(name, task);
    }

    /**
     * Flattens schedule params.
     * <p>
     * Schedule params may have compisite tasks with subtasks. After `flatten`, it will only contain tasks at the main level,
     * with translated names and dependancies, so it works exactly the same. This step is required in order to be able to create actual schedule.
     */
    public void flatten() {
        Map<String, TaskParams> flat = new HashMap<>();
        tasks.forEach((name, task) -> flat.putAll(task.flatten(name)));
        tasks = flat;
    }

    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if schedule parameter is invalid.
     */
    public void validate() {
        tasks.values().forEach(TaskParams::validate);
    }

    /**
     * Populates an existing {@link Scheduler} with tasks from this {@link ScheduleParams}.
     *
     * @param generation Generation for which create that scheduler
     * @param schedule {@link Schedule} to populate
     */
    public void populate(Generation generation, Schedule schedule) {
        // Flatten schedule
        flatten();

        // Create actual tasks
        tasks.forEach((name, params) -> schedule.addTask(name, params.create(generation)));

        // Once all tasks created, we can add dependencies
        tasks.forEach((name, task) -> {
            for (String after : task.after)
                schedule.addDependency(name, after);
        });

        // Once all tasks created, we can add dependencies
        tasks.forEach((name, params) -> {
            for (String after : params.after)
                schedule.addDependency(name, after);
        });
    }

    /**
     * Creates a {@link Scheduler} from this {@link ScheduleParams}.
     *
     * @param generation Generation for which create that scheduler
     * @return resulting {@link Scheduler}.
     */
    public Scheduler create(Generation generation) {
        Scheduler scheduler = new Scheduler();
        populate(generation, scheduler);
        return scheduler;
    }
}

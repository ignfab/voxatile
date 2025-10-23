package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a task running other tasks, parallelized, with dependencies between them (like in a schedule).
 * <p>
 * This params class only instanciates a {@link NoOperationTask} but it
 * {@link #createAdditionalTaskParams creates additional task params} for its subtasks.
 */
public class ScheduleTaskParams extends ModelTaskParams {

    /**
     * List of subtasks indexed by their names.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public Map<String, TileTaskParams> tasks;

    /**
     * List of imported dependencies (names of external tasks that could be used as a dependencies for subtasks).
     */
    @JsonSetter(nulls = Nulls.SKIP, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> using = new HashSet<>();

    /**
     * Creates a new {@code ScheduleTaskParams} with required fields.
     *
     * @param tasks Subtasks indexed names.
     */
    @ConstructorProperties("tasks")
    public ScheduleTaskParams(Map<String, TileTaskParams> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();

        using.forEach(TileTaskParams::validateTaskName);

        tasks.forEach((name, subtask) -> {
            validateTaskName(name);
            if (using.contains(name))
                throw new IllegalArgumentException(
                    "Cannot use \"%s\" as task name as it is already in using list"
                    .formatted(name)
                );
            subtask.validate();
        });
    }

    @Override
    public Map<String, TileTaskParams> createAdditionalTaskParams(String prefix) {
        // Resulting task params indexed by name
        Map<String, TileTaskParams> result = new HashMap<>();

        // Set of tasks known to be followed by another task
        Set<String> followed = new HashSet<>();

        // Flatten internal schedule
        tasks.forEach((name, task) -> {
            task.createAdditionalTaskParams(name).forEach((addname, addtask) -> {
                 result.put(prefix + SEPARATOR + addname, addtask);
            });
            result.put(prefix + SEPARATOR + name, task);
        });

        // Merge model selections
        for (TileTaskParams task : result.values())
            if (task instanceof ModelTaskParams modelTask)
                modelTask.models.narrowDown(models);

        // Translate dependencies
        result.forEach((subname, subtask) -> {
            Set<String> external = new HashSet<>();
            Set<String> internal = new HashSet<>();

            // Process subtask afters: may be internal or external dependencies
            for (String after : subtask.after) {
                if (using.contains(after))
                    // Do not translate external dependencies
                    external.add(after);
                else {
                    // Internal dependencies are translated and checked
                    if (!result.containsKey(prefix + SEPARATOR + after))
                        throw new IllegalArgumentException(
                            "Subtask \"%s\" depends on non existent \"%s\" subtask (may be missing in \"using\")"
                            .formatted(subname, after)
                        );
                    internal.add(prefix + SEPARATOR + after);
                    // Mark followed task
                    followed.add(after);
                }
            }

            // We could add main task dependencies to each subtask but it is useless
            // if subtask already depends on another subtask (i.e. has internal dependencies).
            // We do not know about eventual possible external dependencies simplification.
            subtask.after = new HashSet<>(external);
            subtask.after.addAll(internal.isEmpty() ? after : internal);
        });

        // Replace main task dependencies with all non followed subtasks.
        // Main task is a noop task that will start after all subtasks have ended
        // so it can be safely used as "after" for "after all subtasks ended".
        after = tasks.keySet().stream()
            .filter(Predicate.not(followed::contains))
            .map(name -> prefix + SEPARATOR + name)
            .collect(Collectors.toSet());

        return result;
    }

    @Override
    public TileTask create(Generation generation) {
        return NoOperationTask.INSTANCE;
    }
}

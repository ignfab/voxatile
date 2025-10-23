package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.google.common.collect.Sets;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a task running other tasks, paralelized, with dependencies between them (like in a schedule).
 * <p>
 * This param class does only instanciate a {@link NoOperationTask} but it {@link createAditionalTaskParams}
 * for all subtasks.
 */
public class ScheduleTaskParams extends ModelTaskParams {

    /**
     * List of subclasses indexed by their names.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public Map<String, TileTaskParams> tasks;

    /**
     * List of imported dependencies (name of external task that could be used in subtasks after).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> using = new HashSet<>();

    /**
     * Creates a new {@code SequenceTaskParams} with required fields.
     *
     * @param tasks Subtasks indexed names.
     */
    @ConstructorProperties({"do"})
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
                    "Can not use \"%s\" as task name as it is already in using list"
                    .formatted(name)
                );
            subtask.validate();
        });
    }

    @Override
    public Map<String, TileTaskParams> createAditionalTaskParams(String prefix) {
        // Resulting task params indexed by name
        Map<String, TileTaskParams> result = new HashMap<>();

        // Set of tasks known to be followed by another task
        Set<String> followed = new HashSet<>();

        // Flatten internal schedule
        tasks.forEach((name, task) -> {
            task.createAditionalTaskParams(name).forEach((addname, addtask) -> {
                 result.put(prefix + SEPARATOR + addname, addtask);
            });
            result.put(prefix + SEPARATOR + name, task);
        });

        // Merge model selections
        result.forEach((name, task) -> {
            if (task instanceof ModelTaskParams modelTask)
                modelTask.models.narrowDown(models);
        });

        // Translate dependencies
        result.forEach((subname, subtask) -> {
            Set<String> external = new HashSet<>();
            Set<String> internal = new HashSet<>();

            // Process subtask afters: may be internal or external dependencies
            subtask.after.forEach(name ->  {
                if (this.using.contains(name))
                    // Do not translate external dependencies
                    external.add(name);
                else {
                    // Internal dependencies are translated and checked
                    if (!result.keySet().contains(prefix + SEPARATOR + name))
                        throw new IllegalArgumentException(
                            "Subtask \"%s\" has a dependency to non existant \"%s\" subtask (may be missing in \"using\")"
                            .formatted(subname, name)
                        );
                    internal.add(prefix + SEPARATOR + name);
                    // Mark followed task
                    followed.add(name);
                }
            });

            // We could add main task after to each subtasks but it is useless
            // if subtasks already depends on another subtask (has internal dependencies).
            // We do not know about eventual possible external dependencies simplification.
            subtask.after = Sets.union(external, internal.isEmpty() ? after : internal);
        });

        // Replace main task dependencies with all non followed subtasks.
        // Main task is a noop task that will start after all subtasks have ended
        // so it can be safely used as "after" for "after all subtasks ended".
        after = tasks.keySet().stream()
            .filter(name -> !followed.contains(name))
            .map(name -> prefix + SEPARATOR + name)
            .collect(Collectors.toSet());

        return result;
    }

    @Override
    public TileTask create(Generation generation) {
        return NoOperationTask.INSTANCE;
    }
}

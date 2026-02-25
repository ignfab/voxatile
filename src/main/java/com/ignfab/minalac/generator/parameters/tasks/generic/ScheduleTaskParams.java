package com.ignfab.minalac.generator.parameters.tasks.generic;

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
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a task running other tasks, parallelized, with dependencies between them (like in a schedule).
 * <p>
 * This params class is not intended to instanciate a task object using {@link #create(Generation)}.
 * Subtasks params and a {@link NoOperationTaskParams} (end task) will rather be created using {@link #flatten(String)}.
 * Then, these flattened task params can be used to instanciate corresponding task objects.
 *
 * @param <T> tasks execution context type (same as in {@link Task<T>}).
 */
public class ScheduleTaskParams<T> extends TaskParams<T> {
    /**
     * Subtasks indexed by name.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public Map<String, TaskParams<T>> tasks;

    /**
     * Imported dependencies (names of external tasks that could be used as a dependencies for subtasks).
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
    public ScheduleTaskParams(Map<String, TaskParams<T>> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();

        // Validate using and task names
        using.forEach(TaskParams::validateName);
        tasks.keySet().forEach(TaskParams::validateName);

        // Check task names dont conflict with "using" declaration
        tasks.forEach((name, subtask) -> {
            if (using.contains(name))
                throw new IllegalArgumentException(
                    "Cannot use \"%s\" as task name as it is already in using list"
                    .formatted(name)
                );
        });

        // Validate each task
        tasks.values().forEach(TaskParams::validate);

    }

    @Override
    public Map<String, TaskParams<T>> flatten(String mainName) {
        // Resulting task params indexed by name
        Map<String, TaskParams<T>> result = new HashMap<>();

        // Set of tasks known to be followed by another task
        Set<String> followed = new HashSet<>();

        // Flatten internal schedule
        tasks.forEach((name, task) -> {
            // Translate dependencies
            Set<String> external = new HashSet<>();
            Set<String> internal = new HashSet<>();

            // Process subtask afters: may be internal or external dependencies
            for (String after : task.after) {
                if (using.contains(after))
                    // Do not translate external dependencies
                    external.add(after);
                else {
                    // Internal dependencies are translated and checked
                    if (!tasks.containsKey(after))
                        throw new IllegalArgumentException(
                            "Subtask \"%s\" depends on non existent \"%s\" subtask (may be missing in \"using\")"
                            .formatted(name, after)
                        );
                    internal.add(mainName + SEPARATOR + after);
                    // Mark followed task
                    followed.add(after);
                }
            }

            // We could add main task dependencies to each subtask but it is useless
            // if subtask already depends on another subtask (i.e. has internal dependencies).
            // We do not know about eventual possible external dependencies simplification.
            task.after = new HashSet<>(external);
            task.after.addAll(internal.isEmpty() ? after : internal);

            // Flatten subtask
            result.putAll(task.flatten(mainName + SEPARATOR + name));
        });

        // Replace end task dependencies with all non followed subtasks.
        // End task is a NoOperation task that will start after all subtasks have ended
        // so it can be safely used as "after" for "after all subtasks ended".
        TaskParams<T> endTask = new NoOperationTaskParams<>();
        endTask.after = tasks.keySet().stream()
            .filter(Predicate.not(followed::contains))
            .map(name -> mainName + SEPARATOR + name)
            .collect(Collectors.toSet());

        result.put(mainName, endTask);

        return result;
    }

    @Override
    public Task<T> create(Generation generation) {
        // Once flattened, ScheduleTaskParams is replaced by its subtasks params plus a NoOperationParams.
        // It should not be {@code create}d.
        throw new IllegalStateException("A schedule task is not expected to be directly created");
    }

}

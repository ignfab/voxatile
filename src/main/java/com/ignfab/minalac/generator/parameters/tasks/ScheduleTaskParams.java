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
import com.ignfab.minalac.generator.tasks.Task;

/**
 * Parameters for a task running other tasks, parallelized, with dependencies between them (like in a schedule).
 * <p>
 * This params class is not intended to instanciate a task object using {@link #create(Generation)}.
 * Subtasks params and a {@link NoOperationTaskParams} (end task) will rather be created using {@link #flatten(String)}.
 * Then, these flattened task params can be used to instanciate corresponding task objects.
 */
public class ScheduleTaskParams extends ModelTaskParams {

    /**
     * List of subtasks indexed by their names.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public Map<String, TaskParams> tasks;

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
    public ScheduleTaskParams(Map<String, TaskParams> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();

        using.forEach(TaskParams::validateTaskName);

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
        Map<String, TaskParams> result = new HashMap<>();

        // Subtasks known to be followed by another subtask
        Set<String> followed = tasks.values().stream()
            .flatMap((task) -> task.after.stream())
            .filter(Predicate.not(using::contains))
            .collect(Collectors.toSet());

        // Add main task dependancies to all subtasks not following any other subtask
        // (i.e. having some `after` not in `using`).
        // We could add main task dependencies to each subtask but it is useless.
        tasks.values().forEach((task) -> {
            for (String after : task.after)
                if (!using.contains(after))
                    return;
            task.after.addAll(after);
        });

        // Flatten subtasks
        tasks.forEach((name, task) ->
            // We ask each subtask to flatten itself and then perform some translations to resulting tasks
            task.flatten(name).forEach((resultName, resultTask) -> {
                // Translate dependencies
                resultTask.after = resultTask.after.stream()
                    .map((after) -> using.contains(after) ? after : parentName + SEPARATOR + after)
                    .collect(Collectors.toSet());

                // Merge model selection
                if (resultTask instanceof ModelTaskParams modelTask)
                    modelTask.models.narrowDown(models);

                // Translate name
                result.put(parentName + SEPARATOR + resultName, resultTask);
            })
        );

        // Replace end task dependencies with all non followed subtasks.
        // End task is a NoOperation task that will start after all subtasks have ended
        // so it can be safely used as "after" for "after all subtasks ended".
        TaskParams endTask = new NoOperationTaskParams();
        endTask.after = tasks.keySet().stream()
            .filter(Predicate.not(followed::contains))
            .map(name -> parentName + SEPARATOR + name)
            .collect(Collectors.toSet());

        result.put(parentName, endTask);

        System.out.println("\nSchedule Flattened(" + parentName+")");
        result.forEach((name, task) -> {
            System.out.println("  "+name+":");
            task.after.forEach((after) -> System.out.println("    - "+after));
        });
        return result;
    }

    @Override
    public Task create(Generation generation) {
        // Once flattened, SequenceTaskParams is replaced by its subtasks params plus a NoOperationParams.
        // It should not be {@code create}d.
        throw new IllegalStateException("A sequence task is not expected to be directly created");
    }
}

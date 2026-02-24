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
import com.ignfab.minalac.generator.parameters.tasks.HasModelSelection;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a task running other tasks, parallelized, with dependencies between them (like in a schedule).
 * <p>
 * This params class only instanciates a {@link NoOperationTask} but it
 * {@link #createAdditionalTaskParams creates additional task params} for its subtasks.
 */
public class ScheduleTaskParams<T> extends TaskParams<T> {

    /**
     * Subtasks as a schedule.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonProperty("do")
    public NamedTaskListParams<T> tasks;

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
    public ScheduleTaskParams(NamedTaskListParams<T> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() {
        super.validate();
        tasks.validate();

        using.forEach(TaskParams::validateName);

        // Check task names dont conflict with "using" declaration
        tasks.tasks.forEach((name, subtask) -> {
            if (using.contains(name))
                throw new IllegalArgumentException(
                    "Cannot use \"%s\" as task name as it is already in using list"
                    .formatted(name)
                );
        });
    }

    @Override
    public Map<String, TaskParams<T>> flatten(String mainName) {
        // Resulting task params indexed by name
        Map<String, TaskParams<T>> result = new HashMap<>();

        // Set of tasks known to be followed by another task
        Set<String> followed = new HashSet<>();

        System.out.println("Flatten "+mainName);
        tasks.tasks.forEach((name, task) -> {
            System.out.println("  Task "+name);
            System.out.println("    after: "+task.after);
        });
        // Flatten internal schedule
        tasks.tasks.forEach((name, task) -> {
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
                    if (!tasks.tasks.containsKey(after))
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
            task.flatten(name).forEach((flatName, flatTask) -> {
                 result.put(mainName + SEPARATOR + flatName, flatTask);
            });


            // Avant d'applatir les sous taches, il faut mettre à jour les dépendances du schedule

            // Peut être d'abord applatir le schedule puis maj les dépendances et enfin applatir les sous taches.

            // Il ne faut garder dans result que le résultat de l'applatissement des sous taches + la tache de fin


        });
/* TODO LATER
        // Merge model selections
        if (this instanceof HasModelSelection modelThis)
            for (TaskParams<T> task : result.values())
                if (task instanceof HasModelSelection modelTask)
                    modelTask.models().narrowDown(modelThis.models());
*/

        // Replace main task dependencies with all non followed subtasks.
        // Main task is a noop task that will start after all subtasks have ended
        // so it can be safely used as "after" for "after all subtasks ended".


        TaskParams<T> endTask = new NoOperationTaskParams<>();
        endTask.after = tasks.tasks.keySet().stream()
            .filter(Predicate.not(followed::contains))
            .map(name -> mainName + SEPARATOR + name)
            .collect(Collectors.toSet());

        result.put(mainName, endTask);

        return result;
    }

    @Override
    public Task<T> create(Generation generation) {
        //TODO: Should throw exception
        return NoOperationTask.instance();
    }
}

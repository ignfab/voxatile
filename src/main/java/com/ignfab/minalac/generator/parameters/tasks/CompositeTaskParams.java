package com.ignfab.minalac.generator.parameters.tasks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Abstract params class for a tasks made of subtasks.
 */
public abstract class CompositeTaskParams extends ModelTaskParams {
    /**
     * List of imported dependencies (names of external tasks that could be used as dependencies for subtasks).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> using = new HashSet<>();

    @Override
    public void validate() {
        super.validate();

        using.forEach(TaskParams::validateTaskName);
    }

    /**
     * Populates result task map with a task and its subtasks.
     * <p>
     * If {@code task} is a simple task, this will result in adding it to {@code result}.
     * If it's a composite task, it will be flattened and all resulting tasks will be added.
     *
     * @param result Result task map to populate
     * @param parentName Name of the parent task
     * @param name name of the task to add
     * @param task task to add to results
     */
    protected void populate(Map<String, TaskParams> result, String parentName, String name, TaskParams task) {
        // We ask each subtask to flatten itself and then perform some translations to resulting tasks
        task.flatten(name).forEach((resultName, resultTask) -> {
            // Translate dependencies
            resultTask.after = resultTask.after.stream()
                .map((after) -> using.contains(after) ? after : parentName + SEPARATOR + after)
                .collect(Collectors.toSet());

            // Merge model selections
            if (resultTask instanceof ModelTaskParams modelTask)
                modelTask.models.narrowDown(models);

            // Translate name
            result.put(parentName + SEPARATOR + resultName, resultTask);
        });
    }

    /**
     * Ensures all subtasks start only when main task dependancies are fulfilled.
     * <p>
     * This will add some afters on the right tasks so flat schedule will behave exactly like hierachical schedule described in parameters.
     *
     * @param result Result task list to add afters to
     */
    protected void inheritAfters(Map<String, TaskParams> result) {
        // We could add main dependancies to all subtasks but it is sufficent to add those
        // only to subtasks with no internal dependancies (i.e. having no `after` not in `using`).
        result.values().forEach((task) -> {
            if (using.containsAll(task.after))
                task.after.addAll(after);
        });
    }

    @Override
    public Task create(Generation generation) {
        // Once flattened, CompositeTaskParams is replaced by its subtasks params plus a NoOperationParams.
        // It should never be created using this method.
        throw new IllegalStateException("A composite task is not expected to be directly created");
    }
}

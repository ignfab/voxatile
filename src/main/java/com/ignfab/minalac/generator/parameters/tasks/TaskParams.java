package com.ignfab.minalac.generator.parameters.tasks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a {@link Task}.
 */
public abstract class TaskParams extends PolymorphicParams {
    /**
     * Dependencies (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> after = new HashSet<>();

    @Override
    public void validate() {
        after.forEach(TaskParams::validateTaskName);
    }

    /**
     * Creates the corresponding {@code Task} if there's only one.
     *
     * @param generation the generation context.
     * @return the corresponding task
     */
    public abstract Task create(Generation generation);

    /**
     * Returns a flattened schedule of all subtask, including descendants.
     * <p>
     * This task will be included in the list.
     * Tasks will be renamed to avoid any conflict, prefixed by parent task(s) name(s), separated by {@link #SEPARATOR}.
     * All {@code after}s will be updated accordingly.
     *
     * @param name Name to use for this task
     *
     * @return All subtasks indexed by their name
     */
    public Map<String, TaskParams> flatten(String name) {
        return Map.of(name, this);
    }

    /**
     * Separator used to build subtask names.
     */
    protected static final String SEPARATOR = ":";

    /**
     * Validates a name for a task.
     *
     * @param name task name to validate
     *
     * @throws IllegalArgumentException if task name is invalid
     */
    public static void validateTaskName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Task name cannot be null");
        if (name.isBlank())
            throw new IllegalArgumentException("Task name cannot be blank");
        if (name.contains(SEPARATOR))
            throw new IllegalArgumentException(
                "Task name \"%s\" cannont contain \"%s\" char".formatted(name, SEPARATOR)
            );
    }
}

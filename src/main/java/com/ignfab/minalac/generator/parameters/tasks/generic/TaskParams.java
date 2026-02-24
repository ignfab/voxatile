package com.ignfab.minalac.generator.parameters.tasks.generic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.utils.execution.Task;

public abstract class TaskParams<T> extends PolymorphicParams {
    /**
     * Separator used to build subtask names.
     */
    public static final String SEPARATOR = ":";

    /**
     * Dependencies (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> after = new HashSet<>();

    /**
     * {@code after} field setter.
     * <p>
     * This setter forbids usage of {@code :} char in task name.
     * This cannot be done in {@link #validate()} because it is called after tasks have been
     * added by control tasks (and they have {@code :} chars in their name).
     */
    @JsonProperty("after")
    public void setAfter(Set<String> values) {
        values.forEach(TaskParams::validateName);
        after.addAll(values);
    }

    public abstract Task<T> create(Generation generation);

    /**
     * Create additional {@link TaskParams} if task needs to.
     * This is the case of {@link ScheduleTaskParams} and {@link SequenceTaskParams}.
     * <p>
     * Warning: this may change current object and should be used only once during
     * schedule creation from params.
     *
     * @param prefix prefix added to eventual subtask names
     * @return additional tasks indexed by their name
     */
    public Map<String, ? extends TaskParams<T>> createAdditionalTaskParams(String prefix) {
        return Map.of();
    }

    public static void validateName(String name) {
        if (name.contains(SEPARATOR))
        throw new IllegalArgumentException(
            "Task name \"%s\" contains invalid \"%s\" char".formatted(name, SEPARATOR)
        );
    }

}

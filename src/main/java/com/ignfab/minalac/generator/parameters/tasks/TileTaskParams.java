package com.ignfab.minalac.generator.parameters.tasks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.parameters.TileScheduleParams;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Abstract class for parameters of all {@link TileTask}.
 */
public abstract class TileTaskParams extends PolymorphicParams {
    /**
     * Dependencies (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> after = new HashSet<>();

    @Override
    public void validate() {
        after.forEach(TileTaskParams::validateTaskName);
    }

    /**
     * Creates the corresponding {@code TileTask} if there's only one.
     *
     * @param generation the generation context.
     * @return the corresponding task
     */
    public abstract TileTask create(Generation generation);

    /**
     * Create additional {@link TileTaskParams} if task needs to.
     * This is the case of {@link ScheduleTaskParams} and {@link SequenceTaskParams}.
     * <p>
     * Warning: this may change current object and should be used only once during
     * schedule creation from params (see {@link TileScheduleParams#create}).
     *
     * @param prefix prefix added to eventual subtask names
     * @return additional tasks indexed by their name
     */
    public Map<String, TileTaskParams> createAdditionalTaskParams(String prefix) {
        return Map.of();
    }

    /**
     * Separator used to build subtask names.
     */
    protected static final String SEPARATOR = ":";

    protected static void validateTaskName(String name) {
        if (name.contains(SEPARATOR))
            throw new IllegalArgumentException(
                "Task name \"%s\" contains invalid \"%s\" char".formatted(name, SEPARATOR)
            );
    }

}

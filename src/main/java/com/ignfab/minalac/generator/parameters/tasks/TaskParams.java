package com.ignfab.minalac.generator.parameters.tasks;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.tasks.Task;

/**
 * Parameters for a {@link Task}.
 */
public abstract class TaskParams extends PolymorphicParams {
    /**
     * Dependencies (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<String> after = new ArrayList<>();

    /**
     * Creates the corresponding {@code Task}.
     *
     * @param generation the generation context.
     * @return the corresponding task
     */
    public abstract Task create(Generation generation);
}

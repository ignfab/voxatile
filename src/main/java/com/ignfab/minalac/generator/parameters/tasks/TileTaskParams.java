package com.ignfab.minalac.generator.parameters.tasks;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
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
    public List<String> after = new ArrayList<>();

    /**
     * Model types to apply margin on (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<String> addMarginsTo = new ArrayList<>();

    /**
     * Creates the corresponding {@code TileTask}.
     *
     * @param generation the generation context.
     * @return the corresponding task
     */
    public abstract TileTask create(Generation generation);
}

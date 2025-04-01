package com.ignfab.minalac.generator.parameters.tasks;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> after = new ArrayList<>();

    /**
     * Creates the corresponding {@code Renderer}.
     *
     * @param generation the generation context.
     * @return the corresponding renderer
     */
    public abstract TileTask create(Generation generation);
}

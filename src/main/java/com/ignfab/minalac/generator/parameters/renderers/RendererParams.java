package com.ignfab.minalac.generator.parameters.renderers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Represents the parameters of a type of {@link Renderer}.
 */
public abstract class RendererParams extends PolymorphicParams {
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
    public abstract Renderer create(Generation generation);
}

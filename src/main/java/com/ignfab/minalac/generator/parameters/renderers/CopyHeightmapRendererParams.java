package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.StoredHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.renderers.CopyHeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Represents the parameters of a {@link CopyHeightmapRenderer}.
 */
public class CopyHeightmapRendererParams extends RendererParams {
    /**
     * Models to use as a filter (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * The copied heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams from;

    /**
     * The name of the heightmap receiving the values (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StoredHeightmapParams to;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models to use as a filter
     * @param from the copied heightmap
     * @param to the name of the heightmap receiving the values.
     */
    @ConstructorProperties({"models", "from", "to"})
    public CopyHeightmapRendererParams(ModelSelectionParams models, ReadableHeightmapParams from, StoredHeightmapParams to) {
        this.models = models;
        this.from = from;
        this.to = to;
    }

    @Override
    public void validate() {
        models.validate();
        from.validate();
        to.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new CopyHeightmapRenderer(
            models.create(generation.models()),
            from.create(generation),
            to.create(generation)
        );
    }
}

package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.renderers.HeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;

/**
 * Concrete class of {@link RendererParams} representing the parameters of a {@link HeightmapRenderer}.
 */
public class HeightmapRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public ModelSelectionParams models;
    /**
     * The name of the heightmap to use.
     * This field is required during deserialization.
     */
    public String heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to render.
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"models", "heightmap"})
    public HeightmapRendererParams(ModelSelectionParams models, String heightmap) {
        this.models = models;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
        models.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new HeightmapRenderer(
            models.create(generation.models()),
            generation.heightmaps().get(heightmap)
        );
    }
}

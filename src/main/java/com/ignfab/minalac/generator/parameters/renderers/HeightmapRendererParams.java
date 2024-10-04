package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.renderers.HeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;

/**
 * Concrete class of {@link RendererParams} representing the parameters of a {@link HeightmapRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class HeightmapRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * The name of the heightmap to use.
     * This field is required during deserialization.
     */
    public String heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"modelType", "heightmap"})
    public HeightmapRendererParams(String modelType, String heightmap) {
        this.modelType = modelType;
        this.heightmap = heightmap;
    }

    /**
     * {@inheritDoc}
     */
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Renderer create(Generation generation) {
        return new HeightmapRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap));
    }
}

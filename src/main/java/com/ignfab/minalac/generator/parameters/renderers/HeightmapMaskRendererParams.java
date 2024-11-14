package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.HeightmapMaskRenderer;

import java.beans.ConstructorProperties;

/**
 * Parameters for a {@link HeightmapMaskRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class HeightmapMaskRendererParams extends RendererParams {
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
     * The value that will be written on the heightmap.
     * This field is required during deserialization.
     */
    public int value;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the heightmap to use.
     * @param value the value that will be written on the heightmap.
     */
    @ConstructorProperties({"modelType", "heightmap", "value"})
    public HeightmapMaskRendererParams(String modelType, String heightmap, int value) {
        this.modelType = modelType;
        this.heightmap = heightmap;
        this.value = value;
    }

    @Override
    public Renderer create(Generation generation) {
        return new HeightmapMaskRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap),
            value
        );
    }
}

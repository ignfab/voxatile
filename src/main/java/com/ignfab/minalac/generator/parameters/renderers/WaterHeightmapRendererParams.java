package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.renderers.WaterHeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;

/**
 * Parameters for a {@link WaterHeightmapRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class WaterHeightmapRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * The name of the water heightmap to use.
     * This field is required during deserialization.
     */
    public String heightmap;
    /**
     * The depth that will be written on the heightmap.
     * This field is required during deserialization.
     */
    public Integer depth;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the heightmap to use.
     * @param depth the depth that will be written on the heightmap.
     */
    @ConstructorProperties({"modelType", "heightmap", "depth"})
    public WaterHeightmapRendererParams(String modelType, String heightmap, Integer depth) {
        this.modelType = modelType;
        this.heightmap = heightmap;
        this.depth = depth;
    }

    @Override
    public Renderer create(Generation generation) {
        return new WaterHeightmapRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap),
            depth
        );
    }
}

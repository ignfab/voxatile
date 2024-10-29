package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.ColoredGroundRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;

/**
 * Parameters for a {@link ColoredGroundRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ColoredGroundRendererParams extends RendererParams {
    /**
     * The type of models to get color from (required).
     */
    public String modelType;
    /**
     * The name of the heightmap to use (required).
     */
    public String heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to use
     * @param heightmap the name of the heightmap to use
     */
    @ConstructorProperties({"modelType", "heightmap"})
    public ColoredGroundRendererParams(String modelType, String heightmap) {
        this.modelType = modelType;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        return new ColoredGroundRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap),
            generation.world().getFactory()
        );
    }
}

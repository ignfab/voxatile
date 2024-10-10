package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.renderers.NatureRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.world.SemanticType;

public class NatureRendererParams extends RendererParams {
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

    public SemanticType leaf;

    public SemanticType wood;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({ "modelType", "heightmap", "leaf", "wood" })
    public NatureRendererParams(String modelType, String heightmap, SemanticType leaf, SemanticType wood) {
        this.modelType = modelType;
        this.heightmap = heightmap;
        this.leaf = leaf;
        this.wood = wood;
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

    @Override
    public Renderer create(Generation generation) {
        return new NatureRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap),
            generation.world().getFactory().createVoxelType(leaf),
            generation.world().getFactory().createVoxelType(wood)
        );
    }
}

package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.LevelingRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Parameters for {@link LevelingRenderer}.
 */
public class LevelingRendererParams extends RendererParams {
    /**
     * Type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * Name of the ground heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String heightmap;

    /**
     * {@code Placeable} used to fill the space beneath the model,
     * ensuring it connects to the ground and doesn't appear to float (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams filling;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param models type of models to level.
     * @param heightmap name of the ground heightmap to use.
     * @param filling {@code Placeable} to fill leveled areas with.
     */
    @ConstructorProperties({ "models", "heightmap", "filling" })
    public LevelingRendererParams(
        ModelSelectionParams models,
        String heightmap,
        PlaceableParams filling
    ) {
        this.models = models;
        this.heightmap = heightmap;
        this.filling = filling;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isBlank())
            throw new IllegalArgumentException("The 'heightmap' field cannot be empty or contain only whitespace.");
        models.validate();
        filling.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new LevelingRenderer(
            models.create(generation.models()),
            generation.heightmaps().get(heightmap),
            filling.create(generation.seed(), generation.world())
        );
    }
}

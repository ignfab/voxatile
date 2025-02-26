package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.BuildingRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Parameters for {@link BuildingRenderer}.
 */
public class BuildingRendererParams extends RendererParams {
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
     * {@code Placeable} used to render the roof of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams roof;

    /**
     * {@code Placeable} used to render the walls of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams wall;

    /**
     * {@code Placeable} used to render the windows of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams window;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param models type of models to render
     * @param heightmap name of the ground heightmap to use
     * @param roof {@code Placeable} for roofs
     * @param wall {@code Placeable} for walls
     * @param window {@code Placeable} for windows
     */
    @ConstructorProperties({ "models", "heightmap", "roof", "wall", "window" })
    public BuildingRendererParams(
        ModelSelectionParams models,
        String heightmap,
        PlaceableParams roof,
        PlaceableParams wall,
        PlaceableParams window
    ) {
        this.models = models;
        this.heightmap = heightmap;
        this.roof = roof;
        this.wall = wall;
        this.window = window;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isBlank())
            throw new IllegalArgumentException("'heightmap' field cannot be empty or contain only whitespace.");
        roof.validate();
        wall.validate();
        window.validate();
        models.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new BuildingRenderer(
            models.create(generation.models()),
            generation.heightmaps().get(heightmap),
            roof.create(generation.seed(), generation.world()),
            wall.create(generation.seed(), generation.world()),
            window.create(generation.seed(), generation.world())
        );
    }
}

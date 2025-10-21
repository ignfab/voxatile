package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderBuildingsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderBuildingsTask}.
 */
public class RenderBuildingsTaskParams extends ModelTaskParams {

    /**
     * {@code Placeable} used to render the roof of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams roof;

    /**
     * {@code Placeable} used to render walls of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams wall;

    /**
     * {@code Placeable} used to render windows of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams window;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param roof {@code Placeable} for roofs
     * @param wall {@code Placeable} for walls
     * @param window {@code Placeable} for windows
     */
    @ConstructorProperties({ "roof", "wall", "window" })
    public RenderBuildingsTaskParams(
        PlaceableParams roof,
        PlaceableParams wall,
        PlaceableParams window
    ) {
        this.roof = roof;
        this.wall = wall;
        this.window = window;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        roof.validate();
        wall.validate();
        window.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderBuildingsTask(
            models.create(),
            roof.create(generation.seed()),
            wall.create(generation.seed()),
            window.create(generation.seed())
        );
    }
}

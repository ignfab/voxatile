package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderHeightmapRoofsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderHeightmapRoofsTask}.
 */
public class RenderHeightmapRoofsTaskParams extends TileTaskParams {
    /**
     * Type of models to apply a roof (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * Name of the surface heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * {@code Placeable} used to render the roof of the models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams roof;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param models type of models to render
     * @param heightmap name of the surfaxe heightmap to use
     * @param roof {@code Placeable} for roofs
     */
    @ConstructorProperties({ "models", "heightmap", "roof" })
    public RenderHeightmapRoofsTaskParams(
        ModelSelectionParams models,
        ReadableHeightmapParams heightmap,
        PlaceableParams roof
    ) {
        this.models = models;
        this.heightmap = heightmap;
        this.roof = roof;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();
        heightmap.validate();
        roof.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderHeightmapRoofsTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            roof.create(generation.seed())
        );
    }
}

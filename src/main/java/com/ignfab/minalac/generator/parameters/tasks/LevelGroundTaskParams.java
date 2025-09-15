package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.LevelGroundTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link LevelGroundTask}.
 */
public class LevelGroundTaskParams extends TileTaskParams {
    /**
     * Type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * Name of the ground heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WritableHeightmapParams heightmap;

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
    public LevelGroundTaskParams(
        ModelSelectionParams models,
        WritableHeightmapParams heightmap,
        PlaceableParams filling
    ) {
        this.models = models;
        this.heightmap = heightmap;
        this.filling = filling;
    }

    @Override
    public TileTask create(Generation generation) {
        return new LevelGroundTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            filling.create(generation.seed())
        );
    }
}

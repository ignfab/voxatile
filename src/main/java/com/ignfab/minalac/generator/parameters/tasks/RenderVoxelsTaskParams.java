package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderVoxelsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a {@link RenderVoxelsTask}.
 */
public class RenderVoxelsTaskParams extends TileTaskParams {
    /**
     * The models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * Placeable to render at each voxel (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models the models to render
     * @param place placeable to render at each voxel
     */
    @ConstructorProperties({"models", "place"})
    public RenderVoxelsTaskParams(ModelSelectionParams models, PlaceableParams place) {
        this.models = models;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderVoxelsTask(
            models.create(),
            place.create(generation.seed())
        );
    }
}

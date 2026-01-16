package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderVoxelsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a {@link RenderVoxelsTask}.
 */
public class RenderVoxelsTaskParams extends ModelTaskParams {
    /**
     * Placeable to render at each voxel (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place placeable to render at each voxel
     */
    @ConstructorProperties({"place"})
    public RenderVoxelsTaskParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
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

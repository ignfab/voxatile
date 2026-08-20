package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderPointsTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for {@link RenderPointsTask}.
 */
public class RenderPointsTaskParams extends ModelTaskParams {

    /**
     * Placeable to place at points (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    private PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place placeable to place at points
     */
    @ConstructorProperties({"place"})
    public RenderPointsTaskParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        place.validate();
    }

    @Override
    public Task create(Generation generation) {
        return new RenderPointsTask(
            models.create(generation),
            place.create(generation.seed())
        );
    }
}

package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderPoints2dTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for {@link RenderPoints2dTask}.
 */
public class RenderPoints2dTaskParams extends ModelTaskParams {

    /**
     * Placeable to place at points (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    private PlaceableParams place;

    /**
     * Heightmap on which to draw points (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    private ReadableHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place placeable to place at points
     * @param heightmap heightmap on which to draw points
     */
    @ConstructorProperties({"place", "heightmap"})
    public RenderPoints2dTaskParams(PlaceableParams place, ReadableHeightmapParams heightmap) {
        this.place = place;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        place.validate();
        heightmap.validate();
    }

    @Override
    public Task create(Generation generation) {
        return new RenderPoints2dTask(
            models.create(),
            place.create(generation.seed()),
            heightmap.create(generation.heightmaps())
        );
    }
}

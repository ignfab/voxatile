package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderSurfacesTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderSurfacesTask}.
 */
public class RenderSurfacesTaskParams extends ModelTaskParams {
    /**
     * Heightmap to render on (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;
    /**
     * What to place on surface (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap heightmap to render on.
     * @param place what to place on surface.
     */
    @ConstructorProperties({ "heightmap", "place"})
    public RenderSurfacesTaskParams(ReadableHeightmapParams heightmap, PlaceableParams place) {
        this.heightmap = heightmap;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderSurfacesTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            place.create(generation.seed())
        );
    }
}

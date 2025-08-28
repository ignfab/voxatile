package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderSurfacesTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderSurfacesTask}.
 */
public class RenderSurfacesTaskParams extends TileTaskParams {
    /**
     * The type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;
    /**
     * What to place on surfaces (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to render.
     * @param heightmap heightmap to render on.
     * @param place what to place on surface.
     */
    @ConstructorProperties({"models", "heightmap", "place"})
    public RenderSurfacesTaskParams(ModelSelectionParams models, ReadableHeightmapParams heightmap, PlaceableParams place) {
        this.models = models;
        this.heightmap = heightmap;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();
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

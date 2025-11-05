package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderConnectedLinesTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderSConnexLinesTask}.
 */
public class RenderConnectedLinesTaskParams extends TileTaskParams {
    /**
     * Models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;
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

    public Set<Double> distances = Set.of(0.0);

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models Selection of models to render.
     * @param heightmap heightmap to render on.
     * @param place what to place on surface.
     */
    @ConstructorProperties({"models", "heightmap", "place"})
    public RenderConnectedLinesTaskParams(ModelSelectionParams models, ReadableHeightmapParams heightmap, PlaceableParams place) {
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
        return new RenderConnectedLinesTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            place.create(generation.seed()),
            distances
        );
    }
}

package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.NoVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.tasks.RenderVectorsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderVectorsTask}.
 */
public class RenderVectorsTaskParams extends TileTaskParams {
    /**
     * The type of models to render (required).
     */
    public ModelSelectionParams models;
    /**
     * The name of the ground heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;
    /**
     * What to place on shapes (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableParams place;
    /**
     * What to place inside shapes only (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableParams inside;
    /**
     * What to place on shapes borders only (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableParams borders;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to render.
     * @param heightmap the name of the ground heightmap to use.
     */
    @ConstructorProperties({"models", "heightmap"})
    public RenderVectorsTaskParams(ModelSelectionParams models, ReadableHeightmapParams heightmap) {
        this.models = models;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();

        heightmap.validate();

        if (place == null && inside == null && borders == null)
            throw new IllegalArgumentException("At least one of 'place', 'inside' or 'borders' should be specified");

        if (place != null) {
            if (inside != null || borders != null)
                throw new IllegalArgumentException("Incompatible fields: 'place' can't be present along with 'inside' or 'borders'");
            place.validate();
        }

        if (inside != null)
           inside.validate();

        if (borders != null)
            borders.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        Placeable insidePlaceable = NoVoxel.INSTANCE;
        Placeable bordersPlaceable = NoVoxel.INSTANCE;

        if (place != null) {
            insidePlaceable = place.create(generation.seed(), generation.world());
            bordersPlaceable = insidePlaceable;
        } else {
            if (inside != null)
                insidePlaceable = inside.create(generation.seed(), generation.world());
            if (borders != null)
                bordersPlaceable = borders.create(generation.seed(), generation.world());
        }

        return new RenderVectorsTask(
            models.create(generation.models()),
            heightmap.create(generation),
            insidePlaceable,
            bordersPlaceable
        );
    }
}

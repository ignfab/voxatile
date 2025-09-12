package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.tasks.RenderVectorsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderVectorsTask}.
 */
public class RenderVectorsTaskParams extends ModelTaskParams {
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
     * @param heightmap the name of the ground heightmap to use.
     */
    @ConstructorProperties({"heightmap"})
    public RenderVectorsTaskParams(ReadableHeightmapParams heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();

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
        Placeable insidePlaceable = Nothing.INSTANCE;
        Placeable bordersPlaceable = Nothing.INSTANCE;

        if (place != null) {
            insidePlaceable = place.create(generation.seed());
            bordersPlaceable = insidePlaceable;
        } else {
            if (inside != null)
                insidePlaceable = inside.create(generation.seed());
            if (borders != null)
                bordersPlaceable = borders.create(generation.seed());
        }

        return new RenderVectorsTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            insidePlaceable,
            bordersPlaceable
        );
    }
}

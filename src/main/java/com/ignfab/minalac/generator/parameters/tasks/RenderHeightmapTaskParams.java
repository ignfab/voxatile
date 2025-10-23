package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderHeightmapTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderHeightmapTask}.
 * Can be used by providing either at field or both minimum and maximum fields.
 */
public class RenderHeightmapTaskParams extends TileTaskParams {
    /**
     * The heightmap to use.
     */
    public ReadableHeightmapParams at;
    /**
     * The minimum heightmap.
     */
    public ReadableHeightmapParams minimum;
    /**
     * The maximum heightmap.
     */
    public ReadableHeightmapParams maximum;
    /**
     * The material to place (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place the material to place.
     */
    @ConstructorProperties({"place"})
    public RenderHeightmapTaskParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() {
        // Validity of fields combination
        if ((at != null || minimum == null || maximum == null)
            && (at == null || minimum != null || maximum != null))
            throw new IllegalArgumentException("Either at or both minimum and maximum must be provided");

        super.validate();

        if (at != null)
            at.validate();
        else {
            minimum.validate();
            maximum.validate();
        }
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        ReadableHeightmapSpec from;
        ReadableHeightmapSpec to;
        if (at != null) {
            from = at.create(generation.heightmaps());
            to = from;
        } else {
            from = minimum.create(generation.heightmaps());
            to = maximum.create(generation.heightmaps());
        }
        return new RenderHeightmapTask(from, to, place.create(generation.seed()));
    }
}

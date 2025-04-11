package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundReadableHeightmap;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.HeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Represents the parameters of a {@link HeightmapRenderer}.
 * Can be used by providing either at field or both minimum and maximum fields.
 */
public class HeightmapRendererParams extends RendererParams {
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
    public HeightmapRendererParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() {
        // Validity of fields combination
        if ((at != null || minimum == null || maximum == null)
            && (at == null || minimum != null || maximum != null))
            throw new IllegalArgumentException("Either at or both minimum and maximum must be provided");
        if (at != null)
            at.validate();
        else {
            minimum.validate();
            maximum.validate();
        }
        place.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        UnboundReadableHeightmap from;
        UnboundReadableHeightmap to;
        if (at != null) {
            from = at.create(generation.heightmaps());
            to = from;
        } else {
            from = minimum.create(generation.heightmaps());
            to = maximum.create(generation.heightmaps());
        }
        return new HeightmapRenderer(from, to, place.create(generation.seed()));
    }
}

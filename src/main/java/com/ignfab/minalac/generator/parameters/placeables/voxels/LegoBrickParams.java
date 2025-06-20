package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.lego.LegoBrick;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for Lego bricks with only brick reference and optional color.
 */
public class LegoBrickParams extends PlaceableParams {
    /**
     * Brick reference (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String ref;

    /**
     * Brick color ID.
     */
    public int color = 16;

    /**
     * Creates a new {@code LegoBrickParams}.
     *
     * @param ref Brick reference
     */
    @ConstructorProperties({"ref"})
    public LegoBrickParams(String ref) {
        this.ref = ref;
    }

    @Override
    public void validate() {
        if (ref.isBlank())
            throw new IllegalArgumentException("ref should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return new LegoBrick(ref, color);
    }
}

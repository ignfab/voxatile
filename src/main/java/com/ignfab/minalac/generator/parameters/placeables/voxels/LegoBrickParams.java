package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.lego.LegoBrick;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

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
     * Brick color ID (negative value to remove brick).
     */
    public int color = 16;

    /**
     * X-coordinate of the brick placement offset, in LDraw Units.
     */
    public int lduOffsetX = 0;
    /**
     * Y-coordinate of the brick placement offset, in LDraw Units.
     */
    public int lduOffsetY = 0;
    /**
     * Z-coordinate of the brick placement offset, in LDraw Units.
     */
    public int lduOffsetZ = 0;

    /**
     * The rotation angle in degrees around the LDraw Y axis (vertical).
     */
    public double rotationAroundY = 0;

    /**
     * Creates a new {@code LegoBrickParams}.
     *
     * @param ref Brick reference
     */
    @ConstructorProperties("ref")
    public LegoBrickParams(String ref) {
        this.ref = ref;
    }

    @Override
    public void validate() {
        if (color >= 0 && ref.isBlank())
            throw new IllegalArgumentException("ref should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return color < 0 ? LegoBrick.NO_BRICK : new LegoBrick(ref, color, new WorldCoords3d(lduOffsetX, lduOffsetY, lduOffsetZ), rotationAroundY);
    }
}

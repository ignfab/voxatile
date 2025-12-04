package com.ignfab.minalac.generator.parameters.placeables.patterns;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.patterns.PerlinPattern;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for {@link PerlinPattern} placeable.
 */
public class PerlinPatternParams extends PatternParams {
    /**
     * What to place.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed).
     * Note that perlin noise values are not uniform in that interval, but equally distributed around 0.5.
     */
    public double perlin;

    /**
     * Random seed.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String seed = "";

    /**
     * Size of a grid cell (should be positive and below 1 to produce useful results).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double gridCellSize = 0.1;

    /**
     * Whether to compute 2d or 3d perlin noise.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public boolean ignoreZ = false;

    /**
     * Creates a new {@code RandomPatternParams}.
     *
     * @param place what to place
     * @param perlin Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    @ConstructorProperties({"place", "perlin"})
    public PerlinPatternParams(PlaceableParams place, double perlin) {
        this.place = place;
        this.perlin = perlin;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        place.validate();
        if (gridCellSize <= 0 || gridCellSize >= 1) // Not strictly necessary but really better
            throw new IllegalArgumentException("'gridCellSize' must be between 0 and 1 (exclusive)");
    }

    @Override
    public Pattern create(Seed seed) {
        return new PerlinPattern(seed.salt(this.seed), place.create(seed), perlin, gridCellSize, ignoreZ);
    }
}

package com.ignfab.minalac.generator.parameters.placeables.patterns;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.RandomPattern;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for {@link RandomPattern} placeable.
 */
public class RandomPatternParams extends PatternParams {
    /**
     * What to place.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed).
     */
    public double chance;

    /**
     * Random seed.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String seed = "";

    /**
     * Creates a new {@code RandomPatternParams}.
     *
     * @param place what to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    @ConstructorProperties({"place", "chance"})
    public RandomPatternParams(PlaceableParams place, double chance) {
        this.place = place;
        this.chance = chance;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        place.validate();
    }

    @Override
    public Pattern create(Seed seed) {
        return new RandomPattern(seed.salt(this.seed), place.create(seed), chance);
    }
}

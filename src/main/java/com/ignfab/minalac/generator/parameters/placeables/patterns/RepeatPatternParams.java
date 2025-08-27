package com.ignfab.minalac.generator.parameters.placeables.patterns;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.RepeatPattern;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for {@link RepeatPattern} placeable.
 */
public class RepeatPatternParams extends PatternParams {
    /**
     * What to place (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams repeatStructure;

    /**
     * Represents the number of shifts in a structure on the 3 axes.
     */
    public class CoordShifts {
        /**
         * Number of shifts on the X-axis (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int shiftXBy = 0;

        /**
         * Number of shifts on the Y-axis (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int shiftYBy = 0;

        /**
         * Number of shifts on the Z-axis (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int shiftZBy = 0;
    }

    /**
     * Y-axis or/and Z-axis shift for each X-axis change (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public CoordShifts forEachXRepetition = new CoordShifts();

    /**
     * X-axis or/and Z-axis shift for each Y-axis change (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public CoordShifts forEachYRepetition = new CoordShifts();

    /**
     * X-axis or/and Y-axis shift for each Z-axis change (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public CoordShifts forEachZRepetition = new CoordShifts();

    /**
     * Creates a new {@code RepeatPatternParams}.
     *
     * @param repeatStructure what to place
     */
    @ConstructorProperties({ "repeatStructure" })
    public RepeatPatternParams(PlaceableStructureParams repeatStructure) {
        this.repeatStructure = repeatStructure;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        repeatStructure.validate();
    }

    @Override
    public Pattern create(Seed seed) {
        return new RepeatPattern(
            repeatStructure.create(seed),
            forEachXRepetition.shiftYBy,
            forEachXRepetition.shiftZBy,
            forEachYRepetition.shiftXBy,
            forEachYRepetition.shiftZBy,
            forEachZRepetition.shiftXBy,
            forEachZRepetition.shiftYBy
        );
    }
}

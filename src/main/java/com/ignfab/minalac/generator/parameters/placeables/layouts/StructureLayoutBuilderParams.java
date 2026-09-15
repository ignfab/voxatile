package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.StretchableStructureBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for making a {@link PlaceableStructure} resizable by allowing at most one stretchable band of the structure per axis (e.g. a column at x = 2 and a row at z = 1), effectively making it a Layout Builder.
 */
public class StructureLayoutBuilderParams implements LayoutBuilderParams {
    /**
     * The {@link PlaceableStructure} to transform into a LayoutBuilder.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams structure;
    /**
     * Parameters for defining the band along x-axis stretchable. (Optional)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public StretchAxisParams stretchableAlongX = new NonStretchableAxisParams();
    /**
     * Parameters for defining the band along y-axis stretchable. (Optional)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public StretchAxisParams stretchableAlongY = new NonStretchableAxisParams();
    /**
     * Parameters for defining the band along z-axis  stretchable. (Optional)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public StretchAxisParams stretchableAlongZ = new NonStretchableAxisParams();

    /**
     * Creates a new {@code StructureLayoutBuilderParams} out of mandatory parameters.
     *
     * @param structure the {@link PlaceableStructureParams} to transform.
     */
    @ConstructorProperties({ "structure" })
    public StructureLayoutBuilderParams(PlaceableStructureParams structure) {
        this.structure = structure;
    }

    @Override
    public void validate() {
        structure.validate();
        stretchableAlongX.validate();
        stretchableAlongY.validate();
        stretchableAlongZ.validate();
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed, AxesPolicies policies) throws UnbuildableException {
        PlaceableStructure structure = this.structure.create(seed);

        return new StretchableStructureBuilder(
            structure,
            stretchableAlongX.create(Axis.X),
            stretchableAlongY.create(Axis.Y),
            stretchableAlongZ.create(Axis.Z)
        );
    }

    /**
     * Stretch params along a given axis.
     */
    public static class StretchAxisParams {
        /**
         * Position to use for stretching. This position will be repeated (or omitted) according to wanted size.
         */
        @JsonSetter(nulls = Nulls.FAIL)
        public int at;
        /**
         * Minimum number of repetitions (default 1).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int atLeast = 1;
        /**
         * Maximum number of repetitions (default infinite).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int atMost = Integer.MAX_VALUE;

        /**
         * Validates params.
         *
         * @throws IllegalArgumentException if params are invalid
         */
        public void validate() {
        if (at < 0)
            throw new IllegalArgumentException("Stretch position must be positive");
        if (atLeast < 0)
            throw new IllegalArgumentException("Stretch at least must be positive");
        if (atMost < atLeast)
            throw new IllegalArgumentException("Stretch at most must be greater or equals to stretch at least");
        }

        /**
         * Creates corresponding {@link StretchableStructureBuilder.StretchAxis}.
         *
         * @param axis axis to create
         */
        public StretchableStructureBuilder.StretchAxis create(Axis axis) {
            return new StretchableStructureBuilder.StretchAxis(axis, at, atLeast, atMost);
        }
    }

    /**
     * Special non stretchable stretch params
     */
    public static class NonStretchableAxisParams extends StretchAxisParams {
        @Override
        public void validate() {}

        @Override
        public StretchableStructureBuilder.StretchAxis create(Axis axis) {
            return null;
        }
    }
}

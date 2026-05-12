package com.ignfab.minalac.generator.parameters.placeables.layouts;


// TODO-PR-Facade-OLD: Add a single placeable structure

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

// TODO-PR-Facade-OLD:THIS IS MORE THAT IT SEEMS
/**
 * <p>
 * Parameters for making a {@link PlaceableStructure} resizable by allowing at most one stretchable band of the structure per axis (e.g. a column at x = 2 and a row at z = 1), effectively making it a Layout Builder.
 * <p>
 * Usage example:
 * <pre>
 *   structure:
 *     ...
 *   stretchableAlongX:
 *     at: 3
 *   stretchableAlongZ:
 *     at: 2
 *     atLeast: 0
 * </pre>
 */
public class StructureLayoutBuilderParams implements LayoutBuilderParams {
    /**
     * The {@link PlaceableStructure} to transform into a LayoutBuilder.
     */
    public PlaceableStructureParams structure;
    /**
     * Parameters for defining the band along x-axis stretchable. (Optional)
     */
    public StretchAxisParams stretchableAlongX;
    /**
     * Parameters for defining the band along y-axis stretchable. (Optional)
     */
    public StretchAxisParams stretchableAlongY;
    /**
     * Parameters for defining the band along z-axis  stretchable. (Optional)
     */
    public StretchAxisParams stretchableAlongZ;

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
        if (stretchableAlongX != null)
            stretchableAlongX.validate();
        if (stretchableAlongY != null)
            stretchableAlongY.validate();
        if (stretchableAlongZ != null)
            stretchableAlongZ.validate();
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed, AxesPolicies policies) throws UnbuildableException {
        PlaceableStructure structure = this.structure.create(seed);

        StretchableStructureBuilder.StretchAxis x = null;
        StretchableStructureBuilder.StretchAxis y = null;
        StretchableStructureBuilder.StretchAxis z = null;
        if (stretchableAlongX != null)
            x = new StretchableStructureBuilder.StretchAxis(Axis.X, stretchableAlongX.at, stretchableAlongX.atLeast, stretchableAlongX.atMost);
        if (stretchableAlongY != null)
            y = new StretchableStructureBuilder.StretchAxis(Axis.Y, stretchableAlongY.at, stretchableAlongY.atLeast, stretchableAlongY.atMost);
        if (stretchableAlongZ != null)
            z = new StretchableStructureBuilder.StretchAxis(Axis.Z, stretchableAlongZ.at, stretchableAlongZ.atLeast, stretchableAlongZ.atMost);

        return new StretchableStructureBuilder(structure, x, y, z);
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
         * Validates contents.
         */
        public void validate() {
        if (at < 0)
            throw new IllegalArgumentException("Stretch position must be positive");
        if (atLeast < 0)
            throw new IllegalArgumentException("Stretch at least must be positive");
        if (atMost < atLeast)
            throw new IllegalArgumentException("Stretch at most must be greater or equals to stretch at least");
        }
    }
}

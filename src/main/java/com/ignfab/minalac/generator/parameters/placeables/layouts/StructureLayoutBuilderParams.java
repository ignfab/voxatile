package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.ObjectNode;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.layouts.StretchableStructureBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.random.Seed;

// TODO: Add a single placeable structure
/**
 * TODO:THIS IS MORE THAT IT SEEMS
 * <p>
 * Parameters for a {@link LayoutBuilder} that contains only a structure.
 * <p>
 * Usage example:
 * <pre>
 *   ... placeable description ...
 *   stretchX:
 *     at: 3
 *   stretchZ:
 *     at: 2
 *     atLeast: 0
 * </pre>
 */
public class StructureLayoutBuilderParams implements LayoutBuilderParams {
    public PlaceableStructureParams structure;
    public StretchAxisParams stretchableAlongX;
    public StretchAxisParams stretchableAlongY;
    public StretchAxisParams stretchableAlongZ;

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
    public LayoutBuilder createBuilder(Seed seed) throws UnbuildableException {
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
         * Position to use for stretching. This position will be repeated (or ommitted) according to wanted size.
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

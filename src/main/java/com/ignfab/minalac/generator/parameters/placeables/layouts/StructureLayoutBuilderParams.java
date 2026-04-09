package com.ignfab.minalac.generator.parameters.placeables.layouts;

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
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.StructureLayoutBuilder;
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
@JsonDeserialize(using = StructureLayoutBuilderParams.Deserializer.class)
public class StructureLayoutBuilderParams implements LayoutBuilderParams {

    @JsonIgnore
    private PlaceableStructureParams structure;
    @JsonIgnore
    private StretchAxisParams stretchX;
    @JsonIgnore
    private StretchAxisParams stretchY;
    @JsonIgnore
    private StretchAxisParams stretchZ;

    @Override
    public void validate() {
        structure.validate();
        if (stretchX != null)
            stretchX.validate();
        if (stretchY != null)
            stretchY.validate();
        if (stretchZ != null)
            stretchZ.validate();
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed) throws UnbuildableException {
        PlaceableStructure structure = this.structure.create(seed);

        LayoutBuilder builder = new StructureLayoutBuilder(structure);
        if (stretchX != null)
            builder = stretchX.create(structure, builder, Axis.X);
        if (stretchY != null)
            builder = stretchY.create(structure, builder, Axis.Y);
        if (stretchZ != null)
            builder = stretchZ.create(structure, builder, Axis.Z);
        return builder;
    }

    /**
     * A custom deserializer for {@code StructureLayoutBuilderParams}.
     */
    public static class Deserializer extends ValueDeserializer<StructureLayoutBuilderParams> {
        @Override
        public StructureLayoutBuilderParams deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            JsonNode node = parser.readValueAsTree();

            if (node instanceof ObjectNode objectNode) {
                StructureLayoutBuilderParams params = new StructureLayoutBuilderParams();
                params.stretchX = popProperty(context, objectNode, "stretchX", StretchAxisParams.class);
                params.stretchY = popProperty(context, objectNode, "stretchY", StretchAxisParams.class);
                params.stretchZ = popProperty(context, objectNode, "stretchZ", StretchAxisParams.class);
                params.structure = context.readTreeAsValue(objectNode, PlaceableStructureParams.class);
                return params;
            }  else
                throw new InputCoercionException(parser, "Expected a structure", node.asToken(), LayoutBuilderParams.class);
        }
    }

    private static <T> T popProperty(DeserializationContext context, ObjectNode node, String name, Class<T> type) {
        JsonNode property = node.get(name);
        if (property == null)
            return null;
        node.remove(name);
        return context.readTreeAsValue(property, type);
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
         * Minimum number of repetitions (default 1)
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int atLeast = 1;
        /**
         * Maximum number of repetitions (default infinite)
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int atMost = Integer.MAX_VALUE;

        /**
         * Validates contents.
         */
        public void validate() {
        if (at < 0)
            throw new IllegalArgumentException("Stretch position must be positive");
        if (atLeast < 0 )
            throw new IllegalArgumentException("Stretch at least must be positivie");
        if (atMost < atLeast)
            throw new IllegalArgumentException("Stretch at most must be greater or equals to stretch at least");
        }

        /**
         * Creates a {@link LayoutBuilder} stretching an underlying {@link PlaceableStructure} according to these parameters.
         * @param structure Structure to stretch (used for checking validity of `at` value)
         * @param builder Builder that may already stretch given structure in other axes
         * @param axis Concerned axis
         */
        // TODO: Limit builder to stretch builders
        // TODO: Create a class form Stretch Layout that gives the structure size.
        public LayoutBuilder create(PlaceableStructure structure, LayoutBuilder builder, Axis axis) throws UnbuildableException {
            if (at > structure.limits().max().coord(axis) || at < structure.limits().min().coord(axis))
                throw new UnsupportedOperationException("\"at\" value (%d) is outside structure (%d to %d) for axis %s"
                    .formatted(at, structure.limits().min().coord(axis), structure.limits().max().coord(axis), axis));

            if (atLeast == 0 && structure.limits().sizeX() <= 1)
                throw new UnsupportedOperationException("Forbidden to create empty builder"); // TODO: Check that
            return DefaultLayoutBuilder.stretch(builder,axis, at, atLeast, atMost);
        }
    }
}

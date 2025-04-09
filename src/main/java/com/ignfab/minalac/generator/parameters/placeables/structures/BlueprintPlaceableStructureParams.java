package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.NoVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A parameters class variant for {@link PlaceableStructure}.
 * <p>
 * Structure is described with kind of ASCII art and a legend indicating which character represent which placeable.
 * <p>
 * An offset can be specified to shift the whole structure (convenient to define an off-center structure).
 */
public class BlueprintPlaceableStructureParams extends PlaceableStructureParams.Variant {

    /**
     * Offset on x-axis (optional 0).
     */
    public int xOffset = 0;

    /**
     * Offset on y-axis (optional 0).
     */
    public int yOffset = 0;

    /**
     * Offset on z-axis (optional 0).
     */
    public int zOffset = 0;

    /**
     * List of axes in order of appearance in blueprint (required, must match with blueprint data).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<Axis> axes;

    /**
     * Blueprint of structure (required, must match with axes).
     * Each string char should be space or one of {@code with} keys.
     * If only one axis in axes, there should be only one string.
     * If only two axes in axes, there should be only one list of string.
     */
    @JsonDeserialize(using = BlueprintPlaceableStructureParams.BlueprintDeserializer.class)
    public Blueprint blueprint;

    /**
     * Available axes for blueprint.
     */
    public enum Axis {
        /**
         * X-axis.
         */
        @JsonProperty("x")
        X_ASC(new WorldCoords3d(1, 0, 0)),
        /**
         * Y-axis.
         */
        @JsonProperty("y")
        Y_DESC(new WorldCoords3d(0, 1, 0)),
        /**
         * Z-axis.
         */
        @JsonProperty("z")
        Z_DESC(new WorldCoords3d(0, 0, 1));

        /**
         * Direction vector of the axis.
         */
        public final WorldCoords3d direction;

        /**
         * Creates a new axis.
         *
         * @param direction Direction vector of the axis
         */
        Axis(WorldCoords3d direction) {
            this.direction = direction;
        }
    }

    /**
     * Map of character used in structure description with their corresponding placeables (required).
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public Map<Character, PlaceableParams> with;

    /**
     * Internal characters to placeables translation table.
     */
    private Map<Character, Placeable> placeables;

    /**
     * Creates a new {@code BlueprintPlaceableStructureParams}.
     *
     * @param axes List of axes in blueprint structure
     * @param with Required mapping between used characters in data and corresponding placeables
     * @param blueprint Actual blueprint data
     */
    @ConstructorProperties({"axes", "with", "blueprint"})
    public BlueprintPlaceableStructureParams(List<Axis> axes, Map<Character, PlaceableParams> with, Blueprint blueprint) {
        this.axes = axes;
        this.with = with;
        this.blueprint = blueprint;
    }

    @Override
    public void validate() {
        // Validation propagation
        for (PlaceableParams placeable : with.values())
            placeable.validate();

        // Ensure blueprint structure corresponds to number of axes.
        switch (axes.size()) {
            case 1 -> {
                if (blueprint.data1d() == null)
                    throw new IllegalArgumentException("Missing axes in axes regarding to blueprint.");
            }
            case 2 -> {
                if (blueprint.data2d() == null)
                    throw new IllegalArgumentException("Missing axes in axes regarding to blueprint.");
                // Check axes are all different
                if (axes.get(0) == axes.get(1))
                    throw new IllegalArgumentException("Same axis used twice.");
            }
            case 3 -> {
                if (blueprint.data3d() == null)
                    throw new IllegalArgumentException("Missing axes in axes regarding to blueprint.");
                // Check axes are all different
                if (axes.get(0) == axes.get(1) || axes.get(1) == axes.get(2) || axes.get(2) == axes.get(0))
                    throw new IllegalArgumentException("Same axis used twice.");
            }
            case 0 ->
                throw new IllegalArgumentException("At least one axis have to be specified.");
            default ->
                throw new IllegalArgumentException("At most three axes can be specified.");
        }
    }

    @Override
    public void apply(Seed seed, PlaceableStructure structure) {
        // Prepare translation form chars to placeables
        placeables = new HashMap<>();
        // Default space char for no voxel
        placeables.put(' ', NoVoxel.INSTANCE);
        // Create placeables for each `with` keys
        with.forEach((key, value) -> placeables.put(key, value.create(seed)));

        WorldCoords3d position = new WorldCoords3d(xOffset, yOffset, zOffset);

        switch (axes.size()) {
            // Process string along first axis
            case 1 -> process1d(structure, position, axes.get(0), blueprint.data1d());
            // Process list of strings along first axis for list items and second axis for strings chars
            case 2 -> process2d(structure, position, axes.get(0), axes.get(1), blueprint.data2d());
            // Process list of lists of strings, along first axis first,
            // then second and third axes for child list items and final strings chars.
            // We process lines upside down for a more natural reading
            case 3 -> {
                for (Iterator<LinkedList<String>> it = blueprint.data3d().descendingIterator(); it.hasNext();) {
                    process2d(structure, position, axes.get(1), axes.get(2), it.next());
                    position = position.add(axes.get(0).direction);
                }
            }
        }
    }

    private Placeable getPlaceable(char chr) {
        Placeable placeable = placeables.get(chr);
        if (placeable == null)
            throw new IllegalArgumentException("Found unexplained char '%c' in pattern.".formatted(chr));
        return placeable;
    }

    private void process1d(PlaceableStructure structure, WorldCoords3d position, Axis axis, String data1d) {
        WorldCoords3d direction = axis.direction;

        for (char c : data1d.toCharArray()) {
            structure.set(position, getPlaceable(c));
            position = position.add(direction);
        }
    }

    private void process2d(PlaceableStructure structure, WorldCoords3d position, Axis listAxis, Axis stringAxis, LinkedList<String> data2d) {
        WorldCoords3d direction = listAxis.direction;
        // We process lines upside down for a more natural reading
        for (Iterator<String> it = data2d.descendingIterator(); it.hasNext();) {
            process1d(structure, position, stringAxis, it.next());
            position = position.add(direction);
        }
    }

    /**
     * Blueprint section of parameters. Could be either a string, or a list of strings or a list of lists of strings.
     * <p>
     * Only one of {@code data1d}, {@code data2d} or {@code data3d} should be given, others should be null.
     *
     * @param data1d One dimensional blueprint data as string
     * @param data2d Two dimensional blueprint data as list of strings
     * @param data3d Three dimensional blueprint data as list of lists of strings
     */
    public record Blueprint(String data1d, LinkedList<String> data2d, LinkedList<LinkedList<String>> data3d) {}

    /**
     * Custom deserializer for blueprint field.
     *
     * This deserializer ensures that we can have only : a string, a list of strings or a list of lists of strings.
     * A list mixing strings and lists of strings will be rejected.
     */
    private static final class BlueprintDeserializer extends JsonDeserializer<Blueprint> {

        @Override
        public Blueprint deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JacksonException {

            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);

            // Two possible exception thrown under various conditions:
            final InputCoercionException typeError = new InputCoercionException(jp, "Blueprint should be either a string, a list of strings or a list of lists of strings", node.asToken(), PlaceableParams.class);
            final InputCoercionException dimensionError = new InputCoercionException(jp, "Mixed list levels of strings in blueprint", node.asToken(), PlaceableParams.class);

             // One dimensional blueprint (single string)
            if (node.isTextual())
                return new Blueprint(node.asText(), null, null);

            // Two or three dimensional: should be a non empty array
            if (node.isArray() && node.elements().hasNext()) {
                // Inspect first element to tell how many dimensions we have
                JsonNode first = node.elements().next();

                // Two dimensional blueprint (list of strings)
                if (first.isTextual()) {
                    LinkedList<String> list = new LinkedList<>();
                    for (JsonNode text : node) {
                        if (!text.isTextual())
                            throw dimensionError;
                        list.add(text.asText());
                    }
                    return new Blueprint(null, list, null);
                }

                // Three dimensional blueprint (list of lists of strings)
                if (first.isArray()) {
                    LinkedList<LinkedList<String>> result = new LinkedList<>();
                    for (JsonNode array : node) {
                        if (!array.isArray())
                            throw dimensionError;
                        LinkedList<String> list = new LinkedList<>();
                        for (JsonNode text : array) {
                            if (!text.isTextual())
                                throw typeError;
                            list.add(text.asText());
                        }
                        result.add(list);
                    }
                    return new Blueprint(null, null, result);
                }
            }
            // Not string, list of strings nor list of lists of strings
            throw typeError;
        }
    }
}

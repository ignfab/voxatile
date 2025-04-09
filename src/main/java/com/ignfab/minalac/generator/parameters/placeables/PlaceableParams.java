package com.ignfab.minalac.generator.parameters.placeables;

import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.placeables.patterns.PatternParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Base class for all placeable parameters (voxels and structures).
 */
public abstract class PlaceableParams {

    /**
     * Deserializer for PlaceableParams.
     *
     * This deserializer is able to deserialize various forms of {@link PlaceableParams}, depending on {@link OutputFormat}:
     * Nothing, Voxels (short and long description), Structures.
     */
    public static class Deserializer extends JsonDeserializer<PlaceableParams> {
        private OutputFormat format;

        /**
         * Creates a custom deserializer for given output format.
         *
         * @param format {@code OutputFormat} to use for deserialization
         */
        public Deserializer(OutputFormat format) {
            this.format = format;
        }

        @Override
        public PlaceableParams deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JacksonException {

            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);

            if (node.isTextual()) {
                // "Nothing" string stands for NoVoxelParams (places nothing)
                if (node.textValue().equals("nothing"))
                    return new NothingParams();
                // If value is a string, try to serialize other string using "shortcut" format method.
                return format.createVoxelParams(node.textValue());
            }
            if (node.isArray())
                return new CombinedPlaceableParams(node, codec);

            if (!node.isObject())
                throw new InputCoercionException(jp, "Placeable should be either a string, an object or a list of placeables", node.asToken(), PlaceableParams.class);

            if (node.properties().size() == 1) {
                // If value is an object with only one property, test if its one of hardcoded keys
                Entry<String, JsonNode> property = node.properties().iterator().next();
                switch (property.getKey()) {
                    // Another way to tell "Nothing"
                    case "nothing":
                        return new NothingParams();
                    // Explicit way to deserialize voxels (could be handy for disambiguation)
                    case "voxel":
                        return format.createVoxelParams(property.getValue(), codec);
                    // For structures, relies on PlaceableStructureParams type deduction
                    case "structure":
                        return codec.treeToValue(property.getValue(), PlaceableStructureParams.class);
                    // For patterns, relies on PatternParams type deduction
                    case "pattern":
                        return codec.treeToValue(property.getValue(), PatternParams.class);
                }
            }

            // If none of the above fits, fallback to voxel long description
            return format.createVoxelParams(node, codec);
        }
    }

    /**
     * Validates parameters.
     *
     * Should be called wherever used from another {@code validate} method.
     *
     * @throws IllegalArgumentException if parameters are not valid.
     */
     public void validate() throws IllegalArgumentException {}

    /**
     * Creates a new {@code Placeable} out of parameters.
     *
     * @param seed Random seed to use for this {@code Placeable}.
     *
     * @return Created {@code Placeable}
     */
    public abstract Placeable create(Seed seed);
}

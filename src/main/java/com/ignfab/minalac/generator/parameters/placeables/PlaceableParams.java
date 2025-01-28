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
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.NoVoxelParams;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Base class for all placeable parameters (voxels and structures).
 */
public abstract class PlaceableParams {

    /**
     * Deserializer for PlaceableParams.
     *
     * {@link OutputFormat} class takes care of making {@code PlaceableParams} deserializable by this deserializer.
     *
     * This deserializer is able to perform deserialization in three different ways:
     * 1 - If value is a string, uses the "shortcut" way, using {@link OutputFormat#createVoxelTypeParams(String)} format method.
     * 2 - If value is an object without {@code type} attribute, performs "default" deserialization, using the other form of
     *     {@link OutputFormat#createVoxelTypeParams(JsonNode, ObjectCodec)} format method.
     * 3 - Otherwise, deserialize object using Jackson typeinfo mechanism.
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
                if (node.textValue().equals("nothing"))
                    return new NoVoxelParams();

                return format.createVoxelTypeParams(node.textValue());
            }
            if (!node.isObject())
                throw new InputCoercionException(jp, "Placeable should be either a string or an object", node.asToken(), PlaceableParams.class);

            if (node.properties().size() == 1) {
                Entry<String, JsonNode> property = node.properties().iterator().next();
                switch (property.getKey()) {
                    case "nothing":
                        return new NoVoxelParams();
                    case "voxel":
                        return format.createVoxelTypeParams(property.getValue(), codec);
                    case "structure":
                        return codec.treeToValue(property.getValue(), PlaceableStructureParams.class);
/* TODO later when developping patterns:
                    case "pattern":
                        return codec.treeToValue(property.getValue(), PatternParams.class); */
                }
            }

            return format.createVoxelTypeParams(node, codec);
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
     * @param world World for which {@code Placeable} is created.
     *
     * @return Created {@code Placeable}
     */
    public abstract Placeable create(VoxelWorld world);
}

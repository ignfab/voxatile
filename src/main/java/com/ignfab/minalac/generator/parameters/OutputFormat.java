package com.ignfab.minalac.generator.parameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Output format parameters.
 * Actually, deserialized from a string containing the registered format name.
 */
public class OutputFormat {
    private Supplier<VoxelWorld> worldCreator;
    private Class<? extends PlaceableParams> voxelTypeParams;
    private Function<String, PlaceableParams> shortcutVoxelTypeParams;

    /**
     * Creates a new {@code OutputFormat}.
     *
     * @param worldCreator A static method creating a new VoxelWorld for this format
     * @param voxelTypeParams {@code PlaceableParams} class to use for deserialization when no {@code type} parameter given
     * @param shortcutVoxelTypeParams Method creating a {@code PlaceableParams} from a String
     */
    public OutputFormat(
        Supplier<VoxelWorld> worldCreator,
        Class<? extends PlaceableParams> voxelTypeParams,
        Function<String, PlaceableParams> shortcutVoxelTypeParams
    ) {
        this.worldCreator = worldCreator;
        this.voxelTypeParams = voxelTypeParams;
        this.shortcutVoxelTypeParams = shortcutVoxelTypeParams;
    }

    /**
     * Creates a {@code VoxelWorld} for this format.
     *
     * @return A new {@code VoxelWorld}
     */
    public VoxelWorld createWorld() {
        return worldCreator.get();
    }

    /**
     * Creates a new {@code PlaceableParams} out of a {@code String} if a {@code shortcutVoxelTypeParams} method have been given to constructor.
     *
     * @param text String to interpret as {@code PlaceableParams}
     *
     * @return A new {@code PlaceableParams}
     */
    public PlaceableParams createVoxelTypeParams(String text) {
        if (shortcutVoxelTypeParams == null)
            throw new IllegalArgumentException("Selected format does not allow deserialization from simple string");

        return shortcutVoxelTypeParams.apply(text);
    }

    /**
     * Creates a new {@code PlaceableParams} out of a {@code JsonNode} if a {@code voxelTypeParams} class have been given to constructor.
     *
     * @param node {@code JsonNode} to interpret as {@code PlaceableParams}
     * @param codec Codec to use for deserialization
     *
     * @return A new {@code PlaceableParams}
     */
    public PlaceableParams createVoxelTypeParams(JsonNode node, ObjectCodec codec) throws JsonProcessingException {
        if (voxelTypeParams == null)
            throw new IllegalArgumentException("Selected format does not have a default voxel type structure, add `type` attribute");

        return codec.treeToValue(node, voxelTypeParams);
    };

    /**
     * Register stuff needed for format deserialization.
     *
     * @param mapper {@code ObjectMapper} into which register stuff
     */
    public void registerPlaceableDeserializer(ObjectMapper mapper) {
        // Register format specific deserializer
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PlaceableParams.class, new PlaceableParams.Deserializer(this));
        mapper.registerModule(module);

        // Register base "voxel" type
        if (voxelTypeParams != null)
            mapper.registerSubtypes(new NamedType(voxelTypeParams, "voxel"));
    }

    /**
     * A custom deserializer for {@code OutputFormat}s.
     *
     * It maps format name to a {@code OutputFormat} object.
     */
    static class Deserializer extends JsonDeserializer<OutputFormat> {

        private final Map<String, OutputFormat> formats = new HashMap<>();

        public void registerFormat(String name, OutputFormat format) {
            formats.put(name, format);
        }

        @Override
        public OutputFormat deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonMappingException {

            String name = jp.readValueAs(String.class);
            if (!formats.containsKey(name))
                throw new JsonMappingException(jp, "Invalid value for the \"format\" property. Should be one of %s.".formatted(formats.keySet()));

            return formats.get(name);
        }
    }
}

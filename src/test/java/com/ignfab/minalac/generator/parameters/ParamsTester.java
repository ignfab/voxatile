package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;

/**
 * A utility class for testing deserialization.
 * It provides deserialization utilities configured with a testing output format.
 */
public final class ParamsTester {

    // Make this utility class uninstantiable
    private ParamsTester() {}

    /**
     * Basic testing output format used by {@code deserialize} methods and usable of any test purpose.
     */
    public static final OutputFormat OUTPUT_FORMAT = new OutputFormat(null, TestingVoxelTypeParams.class, TestingVoxelTypeParams::new);

    /**
     * Deserializes parameters with a given output format.
     *
     * @param cls class to deserialize to
     * @param serialized text to deserialize
     * @param format output format to use for deserialization
     * @param mapper mapper to use for deserialization
     *
     * @param <T> type of deserialized object
     *
     * @return deserialized object
     */
    public static <T> T deserialize(Class<T> cls, String serialized, OutputFormat format, ObjectMapper mapper) throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

        SimpleModule module = new SimpleModule("ParamsTesterModule");
        module.setDeserializerModifier(new JsonDelegateDeserialize.BeanModifier());
        mapper.registerModule(module);

        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue(serialized, JsonNode.class);

        return mapper.treeToValue(node, cls);
    }

    /**
     * Deserializes parameters with a given output format.
     *
     * @param cls class to deserialize to
     * @param serialized text to deserialize
     * @param format output format to use for deserialization
     *
     * @param <T> type of deserialized object
     *
     * @return deserialized object
     */
    public static <T> T deserialize(Class<T> cls, String serialized, OutputFormat format) throws JsonProcessingException {
        return deserialize(cls, serialized, format, new ObjectMapper(new YAMLFactory()));
    }


    /**
     * Deserializes parameters with "Testing" output format and given mapper.
     *
     * @param cls class to deserialize to
     * @param serialized text to deserialize
     * @param mapper mapper to use for deserialization
     *
     * @param <T> type of deserialized object
     *
     * @return deserialized object
     */
    public static <T> T deserialize(Class<T> cls, String serialized, ObjectMapper mapper) throws JsonProcessingException {
        return deserialize(cls, serialized, OUTPUT_FORMAT, mapper);
    }

    /**
     * Deserializes parameters with "Testing" output format.
     *
     * @param cls class to deserialize to
     * @param serialized text to deserialize
     *
     * @param <T> type of deserialized object
     *
     * @return deserialized object
     */
    public static <T> T deserialize(Class<T> cls, String serialized) throws JsonProcessingException {
        return deserialize(cls, serialized, OUTPUT_FORMAT, new ObjectMapper(new YAMLFactory()));
    }
}

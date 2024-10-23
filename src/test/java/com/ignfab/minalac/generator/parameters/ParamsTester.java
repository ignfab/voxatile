package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ignfab.minalac.generator.parameters.placeables.TestingVoxelTypeParams;

/**
 * A utility class for testing deserialization.
 */
public final class ParamsTester {

    // Make this utility class uninstantiable
    private ParamsTester() {}

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

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

        new OutputFormat(
            null,
            TestingVoxelTypeParams.class,
            TestingVoxelTypeParams::new
        ).registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue(serialized, JsonNode.class);

        return mapper.treeToValue(node, cls);
    }

}

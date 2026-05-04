package com.ignfab.minalac.generator.parameters;

import java.awt.Color;

import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.dataformat.yaml.YAMLMapper;

import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

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
    public static final OutputFormat OUTPUT_FORMAT = new OutputFormat(null, TestingVoxelParams.class, TestingVoxelParams::new);

    /**
     * Deserializes parameters with a given output format.
     *
     * @param cls class to deserialize to
     * @param serialized text to deserialize
     * @param format output format to use for deserialization
     * @param builder mapper builder to use for deserialization
     *
     * @param <T> type of deserialized object
     *
     * @return deserialized object
     */
    public static <T> T deserialize(Class<T> cls, String serialized, OutputFormat format, MapperBuilder<?, ?> builder) throws JacksonException {
        builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        builder.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        builder.enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION);

        SimpleModule module = new SimpleModule("ParamsTesterModule");
        module.addDeserializer(Color.class, new ColorDeserializer());
        module.setDeserializerModifier(new JsonDelegateDeserialize.BeanModifier());
        builder.addModule(module);

        format.registerPlaceableDeserializer(builder);

        ObjectMapper mapper = builder.build();
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
    public static <T> T deserialize(Class<T> cls, String serialized, OutputFormat format) throws JacksonException {
        return deserialize(cls, serialized, format, YAMLMapper.builder());
    }


    /**
     * Deserializes parameters with "Testing" output format and given mapper.
     *
     * @param cls class to deserialize to
     * @param serialized text to deserialize
     * @param builder mapper builder to use for deserialization
     *
     * @param <T> type of deserialized object
     *
     * @return deserialized object
     */
    public static <T> T deserialize(Class<T> cls, String serialized, MapperBuilder<?, ?> builder) throws JacksonException {
        return deserialize(cls, serialized, OUTPUT_FORMAT, builder);
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
    public static <T> T deserialize(Class<T> cls, String serialized) throws JacksonException {
        return deserialize(cls, serialized, OUTPUT_FORMAT, YAMLMapper.builder());
    }

    /**
     * Creates a new {@link YAMLMapper.Builder} with a params class pre-registered.
     * @param type Type of the params
     * @param clazz Params class to use
     * @return A new mapper builder
     * @see ParamsParser#registerParams(String, Class)
     */
    public static YAMLMapper.Builder mapperBuilderWithParams(String type, Class<? extends PolymorphicParams> clazz) {
        return YAMLMapper.builder().registerSubtypes(new NamedType(clazz, type));
    }

    /**
     * Creates a new {@link YAMLMapper} with a params class pre-registered.
     * @param type Type of the params
     * @param clazz Params class to use
     * @return A new mapper ready to use
     * @see ParamsParser#registerParams(String, Class)
     */
    public static YAMLMapper mapperWithParams(String type, Class<? extends PolymorphicParams> clazz) {
        return mapperBuilderWithParams(type, clazz).build();
    }
}

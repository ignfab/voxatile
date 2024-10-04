package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * A Json/Yaml parser able to decode parameters into a {@code Generation} object.
 */
public class ParamsParser {
    private final ObjectMapper mapper;

    /**
     * Creates a new Parser.
     */
    public ParamsParser() {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        // To prevent duplicates in map type parameters.
        mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }

    /**
     * Parses the given serialized string into a {@link GenerationParams}.
     *
     * @param serialized A string containing the generation parameters data in Json or Yaml format
     * @return the corresponding generation parameters object.
     * @throws ParseException if an error occurs during deserialization such as an invalid structure
     */
    public GenerationParams parse(String serialized) throws ParseException {
        GenerationParams params;
        try {
            params = mapper.readValue(serialized, GenerationParams.class);
        } catch (JsonProcessingException e) {
            throw new ParseException(e);
        }
        try {
            params.validate();
        } catch (IllegalArgumentException e) {
            throw new ParseException(e);
        }
        return params;
    }

    /**
     * Registers a new {@link PolymorphicParams}.
     *
     * @param name the name used to associate the class during deserialization
     * @param clazz The concrete class extending {@code PolymorphicParams}
     * @param <T> The type of the {@code PolymorphicParams} subclass
     */
    public <T extends PolymorphicParams> void registerParams(String name, Class<T> clazz) {
        mapper.registerSubtypes(new NamedType(clazz, name));
    }
}

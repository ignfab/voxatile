package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLAnchorReplayingFactory;

/**
 * A Json/Yaml parser able to decode parameters into a {@code Generation} object.
 */
public class ParamsParser {
    private final ObjectMapper mapper;
    private final OutputFormat.Deserializer formatDeserializer = new OutputFormat.Deserializer();

    /**
     * Creates a new Parser.
     */
    public ParamsParser() {
        mapper = new ObjectMapper(new YAMLAnchorReplayingFactory());
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
     * @throws JsonProcessingException
     */
    public GenerationParams parse(String serialized) throws ParseException, JsonProcessingException {
        GenerationParams params;
        SimpleModule module;

        // Custom deserializers
        module = new SimpleModule("MinalacParserModule");
        module.addDeserializer(OutputFormat.class, formatDeserializer);
        module.setDeserializerModifier(new JsonDelegateDeserialize.BeanModifier());
        mapper.registerModule(module);

        JsonNode document;
        try {
            document = mapper.readTree(serialized);
        } catch (JsonParseException e) {
            throw new ParseException(e);
        }

        if (!document.has("format"))
            throw new ParseException("Missing format field!");

        // Deserialize output format only
        OutputFormat format = mapper.readValue(document.get("format").asText(), OutputFormat.class);

        // Register format specific deserializer
        format.registerPlaceableDeserializer(mapper);

        // Deserialize the whole parameter object
        try {
            params = mapper.treeToValue(document, GenerationParams.class);
        } catch (MismatchedInputException | ValueInstantiationException e) {
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

    /**
     * Registers a new {@code OutputFormat}.
     *
     * @param name Name of the format to register (will be used for deserialization)
     * @param format Output format to register
     */
    public void registerFormat(String name, OutputFormat format) {
        formatDeserializer.registerFormat(name, format);
    }

}

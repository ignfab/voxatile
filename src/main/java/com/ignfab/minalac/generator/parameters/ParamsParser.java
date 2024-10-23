package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
        SimpleModule module;

        // Custom deserializers
        module = new SimpleModule();
        module.addDeserializer(OutputFormat.class, formatDeserializer);
        mapper.registerModule(module);

        try {
            // Deserialize as generic tree
            JsonNode node = mapper.readValue(serialized, JsonNode.class);

            // Read output format information only
            OutputFormat format = mapper.treeToValue(node.at("/format"), OutputFormat.class);

            // Register format specific deserializer
            format.registerPlaceableDeserializer(mapper);

            // Deserialize the whole parameter object
            params = mapper.treeToValue(node, GenerationParams.class);

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

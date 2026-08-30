package com.ignfab.minalac.generator.parameters;

import java.awt.Color;

import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLAnchorReplayingFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

/**
 * A Json/Yaml parser able to decode parameters into a {@code Generation} object.
 */
public class ParamsParser {
    private final YAMLMapper.Builder mapperBuilder;
    private final OutputFormat.Deserializer formatDeserializer = new OutputFormat.Deserializer();

    /**
     * Creates a new Parser.
     */
    public ParamsParser() {
        mapperBuilder = YAMLMapper.builder(new YAMLAnchorReplayingFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
            // To prevent duplicates in map type parameters.
            .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION);
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

        // Custom deserializers
        MinalacParserModule module = new MinalacParserModule();
        // TODO OutputFormat handling might be relocated to the MinalacParserModule (not sure)
        module.addDeserializer(OutputFormat.class, formatDeserializer);
        mapperBuilder.addModule(module);

        YAMLMapper mapper = mapperBuilder.build();

        JsonNode document;
        try {
            document = mapper.readTree(serialized);
        } catch (JacksonException e) {
            throw new ParseException(e);
        }

        if (!document.has("format"))
            throw new ParseException("Missing format field!");

        // Deserialize output format only
        OutputFormat format = mapper.readValue(document.get("format").asString(), OutputFormat.class);

        // Register format specific deserializer
        YAMLMapper.Builder rebuilder = mapper.rebuild();
        format.registerPlaceableDeserializer(rebuilder);

        // Deserialize the whole parameter object
        try {
            // Rebuild the mapper because format-specific configuration was added
            params = rebuilder.build().treeToValue(document, GenerationParams.class);
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
        mapperBuilder.registerSubtypes(new NamedType(clazz, name));
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

    /**
     * Jackson module used by this parser.
     */
    public static class MinalacParserModule extends SimpleModule {
        /**
         * Creates a new instante of this module.
         */
        public MinalacParserModule() {
            super("MinalacParserModule");
        }

        @Override
        public void setupModule(SetupContext context) {
            addDeserializer(Color.class, new ColorDeserializer());
            super.setupModule(context);
            context.addDeserializerModifier(new JsonDelegateDeserialize.BeanModifier());
            context.addDeserializerModifier(new JsonWrapper.BeanModifier());
        }
    }
}

package com.ignfab.minalac.generator.parameters;

import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

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
     * @throws JsonProcessingException
     */
    public GenerationParams parse(String serialized) throws ParseException, JsonProcessingException {
        // Resolve Yaml anchors and pick up format
        // (Jackson is not able to do it by itself but it is still very good at deserializing)

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDereferenceAliases(true); // Prevents new anchor generation on dumping

        Yaml yaml = new Yaml(loaderOptions, dumperOptions);

        Map<Object, Object> document;
        try {
            document = yaml.load(serialized);
        } catch (DuplicateKeyException | ComposerException e) {
            throw new ParseException(e);
        }

        // Custom deserializers
        SimpleModule module = new SimpleModule("MinalacParserModule");
        module.addDeserializer(OutputFormat.class, formatDeserializer);
        module.setDeserializerModifier(new JsonDelegateDeserialize.BeanModifier());
        mapper.registerModule(module);
        SimpleModule anotherModule = new SimpleModule("MinalacParserModule2_because_why_not?");
        module.setDeserializerModifier(new Params.BeanModifier());
        mapper.registerModule(anotherModule);

        if (!document.containsKey("format"))
            throw new ParseException("Missing format field!");

        // Deserialize output format only
        OutputFormat format = mapper.readValue(yaml.dump(document.get("format")), OutputFormat.class);

        // Register format specific deserializer
        format.registerPlaceableDeserializer(mapper);

        // Deserialize the whole parameter object
        try {
            return mapper.readValue(yaml.dump(document), GenerationParams.class);
        } catch (MismatchedInputException | ValueInstantiationException e) {
            throw new ParseException(e);
        }
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

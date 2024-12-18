package com.ignfab.minalac.generator.parameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Class parsing values into different types according to a given type name.
 *
 * @param <T> target type to convert to
 * @param type target type to convert to
 * @param parser function that performs the conversion
 */
@JsonDeserialize(using = ValueParser.Deserializer.class)
public record ValueParser<T>(Class<T> type, Function<Object, ? extends T> parser) {

    /**
     * Map to register parsers for different types.
     */
    private static final Map<String, ValueParser<?>> PARSERS = new HashMap<>();

    /**
     * Built in Integer parser (named "integer" in Yaml).
     */
    public static final ValueParser<Integer> INTEGER = new ValueParser<>(Integer.class, obj -> Integer.valueOf(obj.toString()));
    /**
     * Built in Double parser (named "decimal" in Yaml).
     */
    public static final ValueParser<Double> DOUBLE = new ValueParser<>(Double.class, obj -> Double.valueOf(obj.toString()));
    /**
     * Built in String parser (named "text" in Yaml).
     */
    public static final ValueParser<String> STRING = new ValueParser<>(String.class, Object::toString);
    /**
     * Built in Boolean parser (named "boolean" in Yaml).
     */
    public static final ValueParser<Boolean> BOOLEAN = new ValueParser<>(Boolean.class, obj -> (obj instanceof Boolean bool) ? bool : switch (obj.toString()) {
        case "true" -> true;
        case "false" -> false;
        default -> throw new IllegalArgumentException("Invalid boolean value '%s'".formatted(obj));
    });

    static {
        // Registration (and so Yaml <-> Java names mapping)
        INTEGER.register("integer");
        DOUBLE.register("decimal");
        STRING.register("text");
        BOOLEAN.register("boolean");
    }

    /**
     * Registers {@code ValueParser} under given name.
     */
    public void register(String name) {
        if (PARSERS.containsKey(name))
            throw new IllegalArgumentException("Parser '%s' already registered".formatted(name));
        PARSERS.put(name, this);
    }

    /**
     * Retrieves a {@code ValueParser} from given name.
     */
    public static ValueParser<?> get(String name) {
        if (!PARSERS.containsKey(name))
            throw new IllegalArgumentException("Invalid parser name '%s'. Should be one of %s.".formatted(name, PARSERS.keySet()));
        return PARSERS.get(name);
    }

    /**
     * Parses given data to parser type.
     */
    public T parse(Object value) {
        return parser.apply(value);
    }

    /**
     * A custom deserializer for {@code ValueParser}s.
     */
    static class Deserializer extends JsonDeserializer<ValueParser<?>> {

        @Override
        public ValueParser<?> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {

            String name = jp.readValueAs(String.class);
            try {
                return ValueParser.get(name);
            } catch (IllegalArgumentException e) {
                throw new JsonMappingException(jp, "Invalid parser name '%s'. Should be one of %s.".formatted(name, PARSERS.keySet()));
            }
        }
    }
}

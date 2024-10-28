package com.ignfab.minalac.generator.parameters.processors.post.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for managing parsers for different types.
 */
public final class ValueParsers {

    /**
     * Parser for a specific type.
     *
     * @param <T> target type to convert to
     * @param type target type to convert to
     * @param parser function that performs the conversion
     */
    public record Parser<T>(Class<T> type, Function<Object, ? extends T> parser) {}

    /**
     * Map to register parsers for different types.
     */
    private static final Map<String, Parser<?>> PARSERS = new HashMap<>();

    // Registers predefined parsers
    static {
        addParser("integer", Integer.class, obj -> Integer.valueOf(obj.toString()));
        addParser("decimal", Double.class, obj -> Double.valueOf(obj.toString()));
        addParser("text", String.class, Object::toString);
        addParser("boolean", Boolean.class, obj -> (obj instanceof Boolean bool) ? bool : switch (obj.toString()) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalArgumentException("The value : " + obj.toString() + " isn't a boolean.");
        });
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ValueParsers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Checks if a parser has been registered for this key.
     *
     * @param key key identifying the parser
     * @throws IllegalArgumentException if the key is blank or no parser is found for the key
     */
    public static void validate(String key) {
        if (key.isBlank())
            throw new IllegalArgumentException("The 'as' field cannot be empty or contain only whitespace.");

        if (!PARSERS.containsKey(key))
            throw new IllegalArgumentException("No parser found for key: " + key + ". Expected one of " + PARSERS.keySet());
    }

    /**
     * Adds a new parser.
     *
     * @param <T> target type for the parser.
     * @param key key identifying the parser
     * @param type target type for the parser
     * @param parser function that performs the parsing
     * @throws IllegalArgumentException if the key already exists
     */
    public static <T> void addParser(String key, Class<T> type, Function<Object, ? extends T> parser) {
        if (PARSERS.containsKey(key))
            throw new IllegalArgumentException("Cannot add, the key is already registered");
        PARSERS.put(key, new Parser<T>(type, parser));
    }

    /**
     * Retrieves the parser associated with the given key.
     *
     * @param <T> target type of the parser
     * @param key key identifying the parser
     * @return parser associated with the key
     * @throws IllegalArgumentException if no parser is found for the key
     */
    public static <T> Parser<T> get(String key) {
        validate(key);
        @SuppressWarnings("unchecked")
        Parser<T> parser = (Parser<T>) PARSERS.get(key);
        return parser;
    }
}

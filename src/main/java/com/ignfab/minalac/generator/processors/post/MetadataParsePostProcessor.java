package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

import java.util.function.Function;

/**
 * Post-processor parsing a metadata value in-place.
 *
 * @param <T> type of parsed value
 */
public class MetadataParsePostProcessor<T> implements PostProcessor<Model, Model> {
    private final String name;
    private final Class<T> type;
    private final Function<Object, ? extends T> parser;
    private final ParsingFailurePolicy ifMissingMetadata;
    private final ParsingFailurePolicy ifParserFails;

    /**
     * Creates a new post-processor parsing metadata {@code name} as a {@code type}.
     *
     * @param name name of the metadata to parse
     * @param type type of parsed value
     * @param parser parsing function to use
     * @param ifMissingMetadata policy to apply when the metadata is absent
     * @param ifParserFails policy to apply when the {@code parser} return error
     */
    public MetadataParsePostProcessor(
        String name,
        Class<T> type,
        Function<Object, ? extends T> parser,
        ParsingFailurePolicy ifMissingMetadata,
        ParsingFailurePolicy ifParserFails
    ) {
        this.name = name;
        this.type = type;
        this.parser = parser;
        this.ifMissingMetadata = ifMissingMetadata;
        this.ifParserFails = ifParserFails;
    }

    @Override
    public Class<? super Model> acceptedModelType() {
        return Model.class;
    }

    @Override
    public Class<? extends Model> processedModelType(Class<? extends Model> inputModelType) {
        return inputModelType;
    }

    @Override
    public Model process(Model model) throws GenerationFailedException, IgnorableException {
        if (!model.hasMetadata(name))
            switch (ifMissingMetadata) {
                case IGNORE, REMOVE_METADATA -> {
                    return model;
                }
                case DISCARD_MODEL -> throw new IgnorableException("Missing metadata: " + name);
                case ERROR -> throw new GenerationFailedException("Missing metadata: " + name);
            }

        Object value = model.getMetadata(name);
        if (type.isInstance(value))
            return model;

        try {
            T parsed = parser.apply(value);
            model.setMetadata(name, parsed);
        } catch (Throwable e) {
            switch (ifParserFails) {
                case IGNORE -> {}
                case REMOVE_METADATA -> model.setMetadata(name, null);
                case DISCARD_MODEL -> throw new IgnorableException("Failed to parse metadata: " + name, e);
                case ERROR -> throw new GenerationFailedException("Failed to parse metadata: " + name, e);
            }
        }
        return model;
    }

    /**
     * Policies about how to handle parsing failure.
     */
    public enum ParsingFailurePolicy {
        /**
         * Ignores the failure, leaving everything untouched.
         */
        IGNORE,
        /**
         * Removes the metadata that caused the failure.
         */
        REMOVE_METADATA,
        /**
         * Throws an {@link IgnorableException} to discard the model.
         */
        DISCARD_MODEL,
        /**
         * Throws an {@link GenerationFailedException} causing a fatal error.
         */
        ERROR
    }
}

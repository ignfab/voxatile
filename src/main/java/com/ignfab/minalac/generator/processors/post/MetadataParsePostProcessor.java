package com.ignfab.minalac.generator.processors.post;

import java.util.function.Function;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor parsing a metadata value in-place.
 *
 * @param <T> type of parsed value
 */
public class MetadataParsePostProcessor<T> extends PostProcessor.Generic {
    private final String name;
    private final Class<T> type;
    private final Function<Object, ? extends T> parser;
    private final FailurePolicy ifMissingMetadata;
    private final FailurePolicy ifParserFails;

    /**
     * Creates a new post-processor parsing metadata {@code name} as a {@code type}.
     *
     * @param type type of parsed value
     * @param name name of the metadata to parse
     * @param parser parsing function to use
     * @param ifMissingMetadata policy to apply when the metadata is absent
     * @param ifParserFails policy to apply when the {@code parser} return error
     */
    public MetadataParsePostProcessor(
        Class<T> type,
        String name,
        Function<Object, ? extends T> parser,
        FailurePolicy ifMissingMetadata,
        FailurePolicy ifParserFails
    ) {
        this.type = type;
        this.name = name;
        this.parser = parser;
        this.ifMissingMetadata = ifMissingMetadata;
        this.ifParserFails = ifParserFails;
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
}

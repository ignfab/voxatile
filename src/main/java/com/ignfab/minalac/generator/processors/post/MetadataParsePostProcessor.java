package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

import java.util.function.Function;

/**
 * Post-processor parsing a metadata value in-place.
 * @param <T> type of parsed value
 */
public class MetadataParsePostProcessor<T> implements PostProcessor<Model, Model> {
    private final String name;
    private final Class<T> type;
    private final Function<Object, ? extends T> parser;
    private final ParsingFailurePolicy failurePolicy;
    private final boolean failWhenMissingMetadata;
    private final boolean failWhenParserReturnNull;

    /**
     * Creates a new post-processor parsing {@code name} as a {@code type}.
     * @param name the name of the metadata to parse
     * @param type the type of parsed value
     * @param parser the parsing function to use
     * @param failurePolicy what to do in case of failure (e.g. exception in {@code parser})
     * @param failWhenMissingMetadata whether the absence of metadata is a failure
     * @param failWhenParserReturnNull whether {@code null} return value from {@code parser} is a failure
     */
    public MetadataParsePostProcessor(
        String name,
        Class<T> type,
        Function<Object, ? extends T> parser,
        ParsingFailurePolicy failurePolicy,
        boolean failWhenMissingMetadata,
        boolean failWhenParserReturnNull
    ) {
        this.name = name;
        this.type = type;
        this.parser = parser;
        this.failurePolicy = failurePolicy;
        this.failWhenMissingMetadata = failWhenMissingMetadata;
        this.failWhenParserReturnNull = failWhenParserReturnNull;
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
        if (model.hasMetadata(name)) {
            Object value = model.getMetadata(name);
            if (type.isAssignableFrom(value.getClass()))
                return model;
            try {
                T parsed = parser.apply(value);
                if (parsed == null && failWhenParserReturnNull)
                    throw new Exception("Parsing function returned null");
                model.setMetadata(name, parsed);
            } catch (Throwable e) {
                switch (failurePolicy) {
                    case IGNORE -> {}
                    case REMOVE_METADATA -> model.setMetadata(name, null);
                    case SKIP_MODEL -> throw new IgnorableException("Failed to parse metadata: " + name, e);
                    case ERROR -> throw new GenerationFailedException("Failed to parse metadata: " + name, e);
                }
            }
        } else if (failWhenMissingMetadata) {
            switch (failurePolicy) {
                case IGNORE, REMOVE_METADATA -> {}
                case SKIP_MODEL -> throw new IgnorableException("Missing metadata: " + name);
                case ERROR -> throw new GenerationFailedException("Missing metadata: " + name);
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
         * Throws an {@link IgnorableException} to skip the model.
         */
        SKIP_MODEL,
        /**
         * Throws an {@link GenerationFailedException} causing a fatal error.
         */
        ERROR
    }
}

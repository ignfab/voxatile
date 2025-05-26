package com.ignfab.minalac.generator.processors.post;

import java.util.function.Function;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor parsing a metadata value in-place.
 *
 * @param <T> type of the resulting value
 */
public class MetadataFunctionPostProcessor<T> extends PostProcessor.Generic {
    private final String name;
    private final Class<T> type;
    private final Function<Object, ? extends T> function;
    private final FailurePolicy ifMissingMetadata;
    private final FailurePolicy ifFunctionFails;

    /**
     * Creates a new post-processor that applies a function on a metadata value.
     *
     * @param type type of the resulting value
     * @param name name of the metadata to process
     * @param function function to apply
     * @param ifMissingMetadata policy to apply when the metadata is absent
     * @param ifFunctionFails policy to apply when the {@code function} returns an error
     */
    public MetadataFunctionPostProcessor(
        Class<T> type,
        String name,
        Function<Object, ? extends T> function,
        FailurePolicy ifMissingMetadata,
        FailurePolicy ifFunctionFails
    ) {
        this.type = type;
        this.name = name;
        this.function = function;
        this.ifMissingMetadata = ifMissingMetadata;
        this.ifFunctionFails = ifFunctionFails;
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
            T parsed = function.apply(value);
            model.setMetadata(name, parsed);
        } catch (Throwable e) {
            switch (ifFunctionFails) {
                case IGNORE -> {}
                case REMOVE_METADATA -> model.setMetadata(name, null);
                case DISCARD_MODEL -> throw new IgnorableException("Failed to process metadata: " + name, e);
                case ERROR -> throw new GenerationFailedException("Failed to process metadata: " + name, e);
            }
        }
        return model;
    }
}

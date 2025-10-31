package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor applying a function to a metadata value.
 *
 * @param <T> type of the resulting value
 */
public class MetadataFunctionPostProcessor<T> extends PostProcessor.Generic {
    private final String name;
    private final ModelFunctionProvider<T> provider;
    private final FailurePolicy ifMissingMetadata;
    private final FailurePolicy ifFunctionFails;

    /**
     * Creates a new post-processor that applies a function on a metadata value.
     *
     * @param name name of the metadata to process
     * @param provider model function provider containing function to apply
     * @param ifMissingMetadata policy to apply when the metadata is absent
     * @param ifFunctionFails policy to apply when the {@code function} returns an error
     */
    public MetadataFunctionPostProcessor(
        String name,
        ModelFunctionProvider<T> provider,
        FailurePolicy ifMissingMetadata,
        FailurePolicy ifFunctionFails
    ) {
        this.name = name;
        this.provider = provider;
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

        try {
            model.setMetadata(name, provider.function(model).apply(value));
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

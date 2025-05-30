package com.ignfab.minalac.generator.processors.post;

import java.util.Map;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor to remap a metadata value to another value.
 *
 * @param <T> type of parsed value
 */
public class MetadataValueMappingPostProcessor<T> extends PostProcessor.Generic {
    private final String name;
    private final FailurePolicy ifMissingMetadata;
    private final Map<String, T> valueMapping;
    private final T defaultValue;
    private final FailurePolicy ifNoMatchFound;

    /**
     * Creates a new post-processor to remap a metadata value to another.
     *
     * @param name name of the metadata to remap
     * @param valueMapping maps each input value to a single output value
     * @param defaultValue value to be applied by default
     * @param ifMissingMetadata define the behavior when get a missing metadata
     * @param ifNoMatchFound define the behavior when have no match with the metadata value in the mappings
     */
    public MetadataValueMappingPostProcessor(
        String name,
        Map<String, T> valueMapping,
        T defaultValue,
        FailurePolicy ifMissingMetadata,
        FailurePolicy ifNoMatchFound
    ) {
        this.name = name;
        this.valueMapping = valueMapping;
        this.defaultValue = defaultValue;
        this.ifMissingMetadata = ifMissingMetadata;
        this.ifNoMatchFound = ifNoMatchFound;
    }

    @Override
    public Model process(Model model) throws GenerationFailedException, IgnorableException {
        if (model.getMetadata(name) == null)
            switch (ifMissingMetadata) {
                case IGNORE, REMOVE_METADATA -> {
                    return model;
                }
                case DISCARD_MODEL -> throw new IgnorableException("Missing metadata: '%s'".formatted(name));
                case ERROR -> throw new GenerationFailedException("Missing metadata: '%s'".formatted(name));
            }

        T result = valueMapping.get(model.getMetadata(name).toString());
        if (result == null) {
            if (defaultValue == null) {
                switch (ifNoMatchFound) {
                    case IGNORE -> {}
                    case REMOVE_METADATA -> model.setMetadata(name, null);
                    case DISCARD_MODEL -> throw new IgnorableException("Can't remap the metadata: '%s'".formatted(name));
                    case ERROR -> throw new GenerationFailedException("Can't remap the metadata: '%s'".formatted(name));
                }
            } else {
                model.setMetadata(name, defaultValue);
            }
        } else {
            model.setMetadata(name, result);
        }
        return model;
    }
}

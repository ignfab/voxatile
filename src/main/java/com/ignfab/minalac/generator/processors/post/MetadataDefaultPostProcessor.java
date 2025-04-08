package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor that applies a default value for a specified metadata.
 */
public class MetadataDefaultPostProcessor extends PostProcessor.Generic {
    private final String name;
    private final Object defaultValue;

    /**
     * Creates a new post-processor with a default value for the specified metadata.
     *
     * @param name the name of the metadata
     * @param defaultValue the default value to use if the metadata is not present
     */
    public MetadataDefaultPostProcessor(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public Model process(Model model) {
        if (!model.hasMetadata(name))
            model.setMetadata(name, defaultValue);
        return model;
    }
}

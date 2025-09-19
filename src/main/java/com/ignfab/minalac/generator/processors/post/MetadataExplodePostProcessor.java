package com.ignfab.minalac.generator.processors.post;

import java.util.Map;

import com.ignfab.minalac.generator.models.Model;

/**
 * Post-processor exploding a nested metadata map into flat values.
 * <p>
 * The same model object is returned after post-processing.
 */
public class MetadataExplodePostProcessor extends PostProcessor.Generic {
    private final String metadata;
    private final String prefix;

    /**
     * Creates a new post-processor exploding {@code metadata}.
     * @param metadata the name of the metadata to explode
     * @param prefix optional prefix to prepend to exploded metadata names, empty if no prefix wanted
     */
    public MetadataExplodePostProcessor(String metadata, String prefix) {
        this.metadata = metadata;
        this.prefix = prefix;
    }

    @Override
    public Model process(Model model) {
        // TODO add a failure policy?
        if (model.getMetadata(metadata) instanceof Map<?, ?> map)
            map.forEach((key, value) -> model.setMetadata(prefix + key, value));
        return model;
    }
}

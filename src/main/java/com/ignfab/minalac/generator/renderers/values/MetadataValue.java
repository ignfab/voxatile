package com.ignfab.minalac.generator.renderers.values;

import com.ignfab.minalac.generator.models.Model;

/**
 * A model value from model metadata.
 *
 * @param <T> Type of the value
 */
public class MetadataValue<T> implements ModelValue<T> {

    private String metadataName;

    /**
     * Creates a new {@MetadataValue}.
     *
     * @param metadataName name of metadata to get value from.
     */
    public MetadataValue(String metadataName) {
        this.metadataName = metadataName;
    }

    @Override
    public T get(Model model) {

        try {
            // Missing generic parsing
            return model.getMetadata(metadataName);
        } catch (ClassCastException e) {
            // TODO: What is the best to do here: ignore or throw ?
            return null;
        }
    }
}

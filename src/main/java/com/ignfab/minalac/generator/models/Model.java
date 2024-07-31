package com.ignfab.minalac.generator.models;

import java.util.HashMap;
import java.util.Map;

/**
 * This {@code class} is intended to evolve. Models are objects that can be
 * rendered.
 */
public abstract class Model {
    /**
     * Additional data about the model.
     */
    private final Map<String, Object> metadata = new HashMap<>();

    /**
     * Tells if a metadata exists.
     *
     * @param name Metadata name
     * @return {@code true} if metadata exists with this name
     */
    public boolean hasMetadata(String name) {
        return metadata.containsKey(name);
    }

    /**
     * Returns the metadata value associated to the given name.
     *
     * @param name Metadata name
     * @return Metadata value or {@code null} if it does not exist
     * @param <T> Wanted type of return value
     * @throws ClassCastException If value can not be cast to wanted type
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String name) {
        return (T) metadata.get(name);
    }

    /**
     * Creates or replaces metadata with the given name.
     *
     * @param name  Metadata name to create or update
     * @param value Value to set (existing value is replaced) or {@code null} to
     *              delete metadata
     */
    public void setMetadata(String name, Object value) {
        if (value == null)
            metadata.remove(name);
        else
            metadata.put(name, value);
    }
}

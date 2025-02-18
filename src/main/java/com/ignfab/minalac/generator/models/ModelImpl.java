package com.ignfab.minalac.generator.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Models are objects that can be rendered.
 * They optionally can have some metadata attached to them.
 */
public abstract class ModelImpl implements Model {
    /**
     * Model metadata.
     */
    private final Map<String, Object> metadata = new HashMap<>();

    @Override
    public boolean hasMetadata(String name) {
        return metadata.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMetadata(String name) {
        return (T) metadata.get(name);
    }

    @Override
    public void setMetadata(String name, Object value) {
        if (value == null)
            metadata.remove(name);
        else
            metadata.put(name, value);
    }
}

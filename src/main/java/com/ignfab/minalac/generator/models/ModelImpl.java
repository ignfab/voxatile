package com.ignfab.minalac.generator.models;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

/**
 * Models are objects that can be rendered.
 * They optionally can have some metadata attached to them.
 */
public abstract class ModelImpl implements Model {
    /**
     * Model metadata.
     */
    private final Map<String, Object> metadata = new HashMap<>();
    /**
     * Converter allowing to convert the coordinates/distances of this model into world (game) coordinates/distances.
     */
    private final MapToWorldConverter converter;

    /**
     * Base constructor for models.
     *
     * @param converter the {@link MapToWorldConverter} to use.
     */
    protected ModelImpl(MapToWorldConverter converter) {
        this.converter = converter;
    }

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

    @Override
    public MapToWorldConverter converter() {
        return this.converter;
    }
}

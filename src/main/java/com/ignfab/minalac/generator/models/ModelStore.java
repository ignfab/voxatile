package com.ignfab.minalac.generator.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic model store, storing and retrieving models by type.
 */
public class ModelStore {
    private final Map<String, List<Model>> byType = new HashMap<>();

    /**
     * Add models of a given type in store.
     * Store is quite dumb, it is not able to retrieve type from model (anyway Models do not have type for now).
     *
     * @param type Type to associate with stored models
     * @param models Models to be stored
     */
    // The method is synchronized to prevent concurrent modification exception
    public synchronized void add(String type, List<Model> models) {
        if (type == null || type.isEmpty())
            throw new IllegalArgumentException("Type must be a non empty string");
        if (models.isEmpty())
            return;

        byType.computeIfAbsent(type, k -> new ArrayList<>()).addAll(models);
    }

    /**
     * List models by type.
     *
     * @param type Type of models to be listed
     * @return List of models of the given type
     */
    public List<Model> getByType(String type) {
        return byType.containsKey(type) ? byType.get(type) : Collections.emptyList();
    }
}

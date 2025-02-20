package com.ignfab.minalac.generator.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A basic model store, storing and retrieving models by type.
 */
public class ModelStore {
    private final Map<String, List<Model>> byType = new HashMap<>();

    /**
     * Add a model of a given type in store.
     * Store is quite dumb, it is not able to retrieve type from model (anyway Models do not have type for now).
     *
     * @param type Type to associate with stored model
     * @param model Model to be stored
     */
    public void add(String type, Model model) {
        List<Model> list = byType.get(type);
        if (list == null) {
            list = new LinkedList<>();
            byType.put(type, list);
        }
        list.add(model);
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

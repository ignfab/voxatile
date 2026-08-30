package com.ignfab.minalac.generator.generation.minimaps;

import java.util.HashMap;
import java.util.Map;

/**
 * A store of {@link Minimap}, indexed by their name.
 */
public class MinimapStore {

    private final Map<String, Minimap> store = new HashMap<>();

    /**
     * Adds a minimap to the store with the specified name.
     *
     * @param name the name of the minimap to add
     * @param minimap the minimap to add
     * @throws IllegalArgumentException if the name is {@code null} or already exists in the store
     */
    public void add(String name, Minimap minimap) {
        if (name == null)
            throw new IllegalArgumentException("Cannot add a minimap with null name");
        if (store.containsKey(name))
            throw new IllegalArgumentException("A minimap named \"%s\" is already in store".formatted(name));
        store.put(name, minimap);
    }

    /**
     * Retrieves a {@link Minimap} corresponding to the specified name.
     *
     * @param name the name of the minimap to retrieve
     * @return resulting {@link Minimap}, or {@code null} if not found.
     */
    public Minimap get(String name) {
        return store.get(name);
    }
}

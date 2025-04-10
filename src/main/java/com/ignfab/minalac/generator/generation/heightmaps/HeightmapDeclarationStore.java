package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * {@code HeightmapDeclaration} store by name.
 */
public class HeightmapDeclarationStore {
    private final Map<String, HeightmapDeclaration> store = new HashMap<>();

    /**
     * Registers a new declaration in store.
     *
     * @param declaration declaration to be added
     * @throws IllegalArgumentException if the declaration name is already registered or if the name is null
     */
    public void add(HeightmapDeclaration declaration) {
        if (declaration.name() == null)
            throw new IllegalArgumentException("Cannot add an declaration with null name");
        if (store.containsKey(declaration.name()))
            throw new IllegalArgumentException("An declaration named \"%s\" is already in store".formatted(declaration.name()));
        store.put(declaration.name(), declaration);
    }

    /**
     * Returns the declaration associated to the given name.
     *
     * @param name the name of the declaration.
     * @return the associated declaration
     * @throws NoSuchElementException if no declaration is associated to the specified name
     */
    public HeightmapDeclaration get(String name) {
        HeightmapDeclaration declaration = store.get(name);
        if (declaration == null)
            throw new NoSuchElementException("Unknown heightmap \"%s\"".formatted(name));
        return declaration;
    }

    /**
     * Returns existing names in store.
     *
     * @return the set of existing names
     *
     */
    public Set<String> names() {
        return store.keySet();
    }

    /**
     * Returns existing declarations in store.
     *
     * @return the collection of existing declarations
     */
    public Collection<HeightmapDeclaration> declarations() {
        return store.values();
    }
}

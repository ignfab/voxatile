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
    private final Map<String, HeightmapDeclaration> byName = new HashMap<>();
    private final Map<WritableHeightmapSpec, HeightmapDeclaration> bySpec = new HashMap<>();

    /**
     * Registers a new declaration in store.
     *
     * @param declaration declaration to be added
     * @throws IllegalArgumentException if the declaration name is already registered or if the name is null
     */
    public void add(HeightmapDeclaration declaration) {
        if (declaration.name() == null)
            throw new IllegalArgumentException("Cannot add an declaration with null name");
        if (byName.containsKey(declaration.name()))
            throw new IllegalArgumentException("A declaration named \"%s\" is already in store".formatted(declaration.name()));
        if (bySpec.containsKey(declaration.spec()))
            throw new IllegalArgumentException("Declaration with same spec already in store");
        byName.put(declaration.name(), declaration);
        bySpec.put(declaration.spec(), declaration);
    }

    /**
     * Returns the declaration associated to a given name.
     *
     * @param name the name of the declaration
     * @return the associated declaration
     * @throws NoSuchElementException if no declaration is associated to the specified name
     */
    public HeightmapDeclaration get(String name) {
        HeightmapDeclaration declaration = byName.get(name);
        if (declaration == null)
            throw new NoSuchElementException("Unknown heightmap \"%s\"".formatted(name));
        return declaration;
    }

    /**
     * Returns the declaration associated to a given spec.
     *
     * @param spec heightmap spec of the declaration
     * @return the associated declaration
     * @throws NoSuchElementException if no declaration is associated to the specified spec
     */
    public HeightmapDeclaration get(WritableHeightmapSpec spec) {
        HeightmapDeclaration declaration = bySpec.get(spec);
        if (declaration == null)
            throw new NoSuchElementException("Unknown heightmap spec");
        return declaration;
    }

    /**
     * {@return the set of existing names in store}
     */
    public Set<String> names() {
        return byName.keySet();
    }

    /**
     * {@return the collection of existing declarations in store}
     */
    public Collection<HeightmapDeclaration> declarations() {
        return byName.values();
    }
}

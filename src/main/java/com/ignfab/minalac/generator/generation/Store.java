package com.ignfab.minalac.generator.generation;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class is a store for {@code T} elements.
 * Each element is identified with a unique name.
 * @param <T> the type of the elements that this class stores
 */
public class Store<T> {
    private final Map<String, T> store = new HashMap<>();

    /**
     * Registers a new element with the given name.
     * Name must be unique, case-sensitive.
     *
     * @param name the name of the element which will be used to identify it
     * @param object the element to be added
     * @throws IllegalArgumentException if the specified name is already registered or if the name is null
     */
    public void add(String name, T object) {
        if (name == null || store.containsKey(name))
            throw new IllegalArgumentException("Illegal name for the element, duplicate or null name: " + name);
        store.put(name, object);
    }

    /**
     * Returns the element associated to the given name.
     *
     * @param name the name of the element.
     * @return the associated element
     * @throws NoSuchElementException if no element is associated to the specified name
     */
    public T get(String name) {
        T object = store.get(name);
        if (object == null)
            throw new NoSuchElementException("The element " + name + " does not exist");
        return object;
    }

    /**
     * Returns the set of existing keys in store.
     *
     * @return the set of existing keys
     */
    public Set<String> keys() {
        return store.keySet();
    }
}

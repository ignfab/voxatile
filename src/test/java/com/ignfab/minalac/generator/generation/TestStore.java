package com.ignfab.minalac.generator.generation;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestStore {
    @Test
    public void testAdd() {
        Store<String> store = new Store<>();

        assertDoesNotThrow(() -> store.add("first", "element"));
        assertDoesNotThrow(() -> store.add("second", "otherElement"));
        assertThrows(IllegalArgumentException.class, () -> store.add("first", "abcd"), "Should not be able to add an element with an existing name");
        assertThrows(IllegalArgumentException.class, () -> store.add(null, "element"), "Should not be able to add an element with a null name");
        assertEquals("element", store.get("first"));
        assertEquals("otherElement", store.get("second"));
    }

    @Test
    public void testGet() {
        Store<String> store = new Store<>();

        store.add("first", "element");
        store.add("second", "good");
        String retrievedElement = assertDoesNotThrow(() -> store.get("second"));
        assertEquals("good", retrievedElement);
        assertThrows(NoSuchElementException.class, () -> store.get("foo"));
    }
}

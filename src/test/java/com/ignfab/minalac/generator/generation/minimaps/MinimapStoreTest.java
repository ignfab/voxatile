package com.ignfab.minalac.generator.generation.minimaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class MinimapStoreTest {
    private MinimapStore store;

    @BeforeEach
    void setUp() {
        store = new MinimapStore();
    }

    @Test
    void testAdd() {
        assertDoesNotThrow(() -> store.add("test", new Minimap(
            new WorldBBox2d(0, 0, 1, 1), 1
        )));

        assertThrows(IllegalArgumentException.class, () -> store.add(null, new Minimap(
            new WorldBBox2d(0, 0, 1, 1), 1
        )));

        assertThrows(IllegalArgumentException.class, () -> store.add("test", new Minimap(
            new WorldBBox2d(0, 0, 1, 1), 1
        )));
    }

    @Test
    void testGet() {
        Minimap expected = new Minimap(new WorldBBox2d(0, 0, 1, 1), 1);
        store.add("getTest", expected);
        assertEquals(expected, assertDoesNotThrow(() -> store.get("getTest")));
    }
}

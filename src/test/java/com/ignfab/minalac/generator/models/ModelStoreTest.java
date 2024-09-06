package com.ignfab.minalac.generator.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelStoreTest {
    static class TestModel extends Model {}

    @Test
    void testAddByType() {
        ModelStore store = new ModelStore();
        store.add("toto", new TestModel());
        store.add("toto", new TestModel());
        store.add("titi", new TestModel());
        assertEquals(2, store.getByType("toto").size());
        assertEquals(1, store.getByType("titi").size());
        assertNull(store.getByType("tata"));
    }
}

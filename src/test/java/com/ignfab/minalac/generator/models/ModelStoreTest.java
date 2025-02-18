package com.ignfab.minalac.generator.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelStoreTest {
    class TestModel extends ModelImpl {
        @Override
        public String salt() {
            return "";
        }
    }

    @Test
    void testAddByType() {
        ModelStore store = new ModelStore();
        store.add("toto", new TestModel());
        store.add("toto", new TestModel());
        store.add("titi", new TestModel());
        assertEquals(2, store.getByType("toto").size());
        assertEquals(1, store.getByType("titi").size());
        assertEquals(0, store.getByType("tata").size());
    }
}

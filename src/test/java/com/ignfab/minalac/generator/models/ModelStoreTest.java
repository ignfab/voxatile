package com.ignfab.minalac.generator.models;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelStoreTest {
    static class TestModel extends ModelImpl {
        @Override
        public String salt() {
            return "";
        }
    }

    @Test
    void testAddByType() {
        ModelStore store = new ModelStore();
        store.add("toto", List.of(new TestModel(), new TestModel()));
        store.add("titi", List.of(new TestModel()));
        assertEquals(2, store.getByType("toto").size());
        assertEquals(1, store.getByType("titi").size());
        assertEquals(0, store.getByType("tata").size());
    }
}

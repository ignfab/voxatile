package com.ignfab.minalac.generator.models.filters;

import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterHasMetadataTest {

    @Test
    public void testIsSelected() {
        Predicate<Model> filter;

        filter = new ModelFilterHasMetadata("a");
        assertTrue(filter.test(new TestingModel(Map.of("a", 1, "b", 2, "c", 3))));
        assertTrue(filter.test(new TestingModel(Map.of("a", 1))));
        assertFalse(filter.test(new TestingModel(Map.of("b", 2))));
    }
}

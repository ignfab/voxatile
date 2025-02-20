package com.ignfab.minalac.generator.models.filters;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterMetadataInTest {
    @Test
    public void testIsSelected() {
        Predicate<Model> filter = new ModelFilterMetadataIn("a", Arrays.asList(1, 2, 3));
        assertTrue(filter.test(new TestingModel(Map.of("a", 1, "b", 2, "c", 3))));
        assertTrue(filter.test(new TestingModel(Map.of("a", 2))));
        assertFalse(filter.test(new TestingModel(Map.of("a", 4))));
        assertFalse(filter.test(new TestingModel(Map.of("b", 1))));
    }
}

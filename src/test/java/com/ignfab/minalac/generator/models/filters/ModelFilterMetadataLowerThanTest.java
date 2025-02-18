package com.ignfab.minalac.generator.models.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

public class ModelFilterMetadataLowerThanTest {
    @Test
    public void testIsSelected() {
        Predicate<Model> filter = new ModelFilterMetadataLowerThan("a", 1.);
        assertTrue(filter.test(new TestingModel(Map.of("a", 0))));
        assertFalse(filter.test(new TestingModel(Map.of("a", 1))));
        assertFalse(filter.test(new TestingModel(Map.of("a", 2))));
        assertFalse(filter.test(new TestingModel(Map.of("a", "0"))));
        assertFalse(filter.test(new TestingModel()));
    }
}

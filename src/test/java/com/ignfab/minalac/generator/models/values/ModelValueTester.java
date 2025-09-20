package com.ignfab.minalac.generator.models.values;

import java.util.Optional;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public final class ModelValueTester {
    private ModelValueTester() {}

    public static void assertModelValueAbsent(ModelValue value) {
        assertModelValueAbsent(value, new TestingModel());
    }

    public static void assertModelValueAbsent(ModelValue value, Model model) {
        assertTrue(value.get(model).isEmpty(), "Model value '%s' expected to be absent on model '%s'".formatted(value, model));
    }

    public static void assertModelValue(ModelValue value, double expected) {
        assertModelValue(value, expected, new TestingModel());
    }

    public static void assertModelValue(ModelValue value, double expected, Model model) {
        Optional<Double> actual = value.get(model);
        assertTrue(actual.isPresent(), "Model value '%s' expected to be present on model '%s'".formatted(value, model));
        assertEquals(expected, actual.get(), "Model value '%s' expected to be '%f' on model '%s'".formatted(value, expected, model));
    }
}

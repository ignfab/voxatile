package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterOrParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterOrParams(List.of()));
        assertDoesNotThrow(() -> new ModelFilterOrParams(List.of(
            TestingModelFilterParams.VALID,
            TestingModelFilterParams.VALID
        )));
    }

    @Test
    public void testValidate() {
        ModelFilterParams params;

        params = new ModelFilterOrParams(List.of(
            TestingModelFilterParams.VALID
        ));
        assertDoesNotThrow(params::validate);

        params = new ModelFilterOrParams(List.of(
            TestingModelFilterParams.VALID,
            TestingModelFilterParams.INVALID
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new ModelFilterOrParams(List.of());
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        Model model1 = new TestingModel("1");
        Model model2 = new TestingModel("2");
        Model model3 = new TestingModel("3");

        Predicate<Model> filter;

        // Check we really have an "OR" at the end

        ModelFilterParams params1 = new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(model2),
            new TestingModelFilterParams(model2),
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1)
        ));
        filter = assertDoesNotThrow(() -> params1.create(TestingGeneration.UNUSED));

        assertTrue(filter.test(model1));
        assertTrue(filter.test(model2));
        assertFalse(filter.test(model3));

        // Check we have same predicate if it is the only element in list

        ModelFilterParams params2 = new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(model1)
        ));
        filter = assertDoesNotThrow(() -> params2.create(TestingGeneration.UNUSED));

        assertTrue(filter.test(model1));
        assertFalse(filter.test(model2));
    }
}

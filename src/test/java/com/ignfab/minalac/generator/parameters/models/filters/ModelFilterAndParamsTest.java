package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterAndParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterAndParams(List.of()));
        assertDoesNotThrow(() -> new ModelFilterAndParams(List.of(
            TestingModelFilterParams.VALID,
            TestingModelFilterParams.VALID
        )));
    }

    @Test
    public void testValidate() {
        ModelFilterParams params;

        params = new ModelFilterAndParams(List.of(
            TestingModelFilterParams.VALID
        ));
        assertDoesNotThrow(params::validate);

        params = new ModelFilterAndParams(List.of(
            TestingModelFilterParams.VALID,
            TestingModelFilterParams.INVALID,
            TestingModelFilterParams.INVALID
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new ModelFilterAndParams(List.of());
        assertThrows(IllegalArgumentException.class, params::validate);
    }


    @Test
    public void testCreate() {
        Model model1 = new TestingModel("1");
        Model model2 = new TestingModel("2");

        Predicate<Model> filter;

        // Check we really have an "AND" at the end

        ModelFilterParams params1 = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1)
        ));
        filter = assertDoesNotThrow(() -> params1.create(TestingGeneration.UNUSED));

        assertTrue(filter.test(model1));
        assertFalse(filter.test(model2));

        ModelFilterParams params2 = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model2),
            new TestingModelFilterParams(model2)
        ));
        filter = assertDoesNotThrow(() -> params2.create(TestingGeneration.UNUSED));

        assertFalse(filter.test(model1));
        assertFalse(filter.test(model2));

        // Check we have same predicate if it is the only element in list

        ModelFilterParams params3 = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(model1)
        ));
        filter = assertDoesNotThrow(() -> params3.create(TestingGeneration.UNUSED));

        assertTrue(filter.test(model1));
        assertFalse(filter.test(model2));
    }
}

package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;

public class ModelFilterOrParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterOrParams(List.of()));
        assertDoesNotThrow(() -> new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(true, null),
            new TestingModelFilterParams(true, null)
        )));
    }

    @Test
    public void testValidate() {
        ModelFilterParams params;

        params = new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(true, null)
        ));
        assertDoesNotThrow(params::validate);

        params = new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(true, null),
            new TestingModelFilterParams(false, null)
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new ModelFilterOrParams(List.of());
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        Model model1 = new TestingModel();
        Model model2 = new TestingModel();
        Model model3 = new TestingModel();

        ModelFilterParams params;
        Predicate<Model> filter;

        // Check we realy have a "OR" at the end

        params = new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(model2),
            new TestingModelFilterParams(model2),
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1)
        ));
        filter = assertDoesNotThrow(params::create);

        assertTrue(filter.test(model1));
        assertTrue(filter.test(model2));
        assertFalse(filter.test(model3));

        // Check we have same predicate if it is the only element in list

        params = new ModelFilterOrParams(List.of(
            new TestingModelFilterParams(true, model1)
        ));
        filter = assertDoesNotThrow(params::create);

        assertTrue(filter.test(model1));
        assertFalse(filter.test(model2));
    }
}

package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Predicate;

public class ModelFilterAndParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterAndParams(List.of()));
        assertDoesNotThrow(() -> new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(),
            new TestingModelFilterParams()
        )));
    }

    @Test
    public void testValidate() {
        ModelFilterParams params;

        params = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(true)
        ));
        assertDoesNotThrow(params::validate);

        params = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(true),
            new TestingModelFilterParams(false),
            new TestingModelFilterParams(false)
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new ModelFilterAndParams(List.of());
        assertThrows(IllegalArgumentException.class, params::validate);
    }


    @Test
    public void testCreate() {
        Model model1 = new TestingModel();
        Model model2 = new TestingModel();

        ModelFilterParams params;
        Predicate<Model> filter;

        // Check we realy have a "AND" at the end

        params = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1)
        ));
        filter = assertDoesNotThrow(params::create);

        assertTrue(filter.test(model1));
        assertFalse(filter.test(model2));

        params = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model1),
            new TestingModelFilterParams(model2),
            new TestingModelFilterParams(model2)
        ));
        filter = assertDoesNotThrow(params::create);

        assertFalse(filter.test(model1));
        assertFalse(filter.test(model2));

        // Check we have same predicate if it is the only element in list

        params = new ModelFilterAndParams(List.of(
            new TestingModelFilterParams(true, model1)
        ));
        filter = assertDoesNotThrow(params::create);

        assertTrue(filter.test(model1));
        assertFalse(filter.test(model2));
    }
}

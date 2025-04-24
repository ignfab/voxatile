package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterNotParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterNotParams(TestingModelFilterParams.VALID));
    }

    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterNotParams(TestingModelFilterParams.VALID);
        ModelFilterParams invalid = new ModelFilterNotParams(TestingModelFilterParams.INVALID);

        assertThrows(IllegalArgumentException.class, invalid::validate);
        assertDoesNotThrow(valid::validate);
    }

    @Test
    public void testCreate() {
        Model model1 = new TestingModel("1");
        Model model2 = new TestingModel("2");

        ModelFilterParams params = new ModelFilterNotParams(new TestingModelFilterParams(model1));

        Predicate<Model> filter = assertDoesNotThrow(params::create);

        // Check filter has been negated
        assertFalse(filter.test(model1));
        assertTrue(filter.test(model2));
    }
}

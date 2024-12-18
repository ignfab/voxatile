package com.ignfab.minalac.generator.parameters.models;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.parameters.models.filters.TestingModelFilterParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelSelectionParamsTest {

    @Test
    public void testValidate() {
        ModelSelectionParams params;

        // Testing basic ModelSelectionParams validation

        params = new ModelSelectionParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new ModelSelectionParams("aa");
        assertDoesNotThrow(params::validate);

        // Testing validation of filter is done and exception transmitted to caller

        params.filter = new TestingModelFilterParams(false);
        assertThrows(IllegalArgumentException.class, params::validate);

        params.filter = new TestingModelFilterParams(true);
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testCreate() {
        ModelSelectionParams params;
        ModelStore store = new ModelStore();

        params = new ModelSelectionParams("aa");
        assertInstanceOf(ModelSelection.class, assertDoesNotThrow(() -> params.create(store)));

        params.filter = new TestingModelFilterParams();
        assertInstanceOf(ModelSelection.class, assertDoesNotThrow(() -> params.create(store)));
    }
}

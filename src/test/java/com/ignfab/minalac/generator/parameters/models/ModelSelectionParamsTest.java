package com.ignfab.minalac.generator.parameters.models;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterAndParams;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterEmptyGeometryParams;
import com.ignfab.minalac.generator.parameters.models.filters.TestingModelFilterParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelSelectionParamsTest {

    @Test
    public void testValidate() {
        ModelSelectionParams params;

        // Testing basic ModelSelectionParams validation

        params = new ModelSelectionParams();
        assertDoesNotThrow(params::validate);

        params.type = "";
        assertThrows(IllegalArgumentException.class, params::validate);

        params.type = "aa";
        assertDoesNotThrow(params::validate);

        // Testing validation of filter is done and exception transmitted to caller

        params.filter = TestingModelFilterParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.filter = TestingModelFilterParams.VALID;
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testCreate() {
        ModelSelectionParams params;

        params = new ModelSelectionParams();
        params.type = "aa";

        assertInstanceOf(ModelSelection.class, assertDoesNotThrow(params::create));

        params.filter = TestingModelFilterParams.VALID;
        assertInstanceOf(ModelSelection.class, assertDoesNotThrow(params::create));
    }

    @Test
    public void testNarrowDown() {
        ModelSelectionParams params;
        ModelSelectionParams narrower;
        ModelFilterEmptyGeometryParams filter = new ModelFilterEmptyGeometryParams();

        // Narrow with nothing: keep the same
        params = new ModelSelectionParams();
        params.type = "A";
        params.filter = filter;

        params.narrowDown(new ModelSelectionParams());

        assertEquals("A", params.type);
        assertInstanceOf(ModelFilterEmptyGeometryParams.class, params.filter);

        // Narrower brings type
        params = new ModelSelectionParams();
        params.filter = filter;

        narrower = new ModelSelectionParams();
        narrower.type = "B";

        params.narrowDown(narrower);

        assertEquals("B", params.type);
        assertInstanceOf(ModelFilterEmptyGeometryParams.class, params.filter);

        // Same type -> same type
        params = new ModelSelectionParams();
        params.type = "C";
        params.filter = filter;

        narrower = new ModelSelectionParams();
        narrower.type = "C";

        params.narrowDown(narrower);

        assertEquals("C", params.type);
        assertInstanceOf(ModelFilterEmptyGeometryParams.class, params.filter);

        // Different types -> NONE selection
        params = new ModelSelectionParams();
        params.type = "D";

        narrower = new ModelSelectionParams();
        narrower.type = "E";

        params.narrowDown(narrower);

        assertEquals(ModelSelection.NONE, params.create());

        // Narrower brings filter
        params = new ModelSelectionParams();
        params.type = "F";

        narrower = new ModelSelectionParams();
        narrower.filter = filter;

        params.narrowDown(narrower);

        assertInstanceOf(ModelFilterEmptyGeometryParams.class, params.filter);

        // Combine filters
        params = new ModelSelectionParams();
        params.type = "F";
        params.filter = filter;

        narrower = new ModelSelectionParams();
        narrower.filter = new ModelFilterEmptyGeometryParams();

        params.narrowDown(narrower);

        assertInstanceOf(ModelFilterAndParams.class, params.filter);

        // Narrow down a None selection
        params = new ModelSelectionParams();
        params.type = "G";

        narrower = new ModelSelectionParams();
        narrower.type = "H";

        params.narrowDown(narrower); // Just create a none selection

        params.narrowDown(new ModelSelectionParams()); // Actual test

        assertEquals(ModelSelection.NONE, params.create());

        // Now test the oposite (do not separate from above test, we reuse instances)
        narrower = params;
        narrower.type = null; // Narrower is now just a none selection with no type and no filter to interfere

        params = new ModelSelectionParams();
        params.type = "I";

        params.narrowDown(narrower);

        assertEquals(ModelSelection.NONE, params.create());

    }
}

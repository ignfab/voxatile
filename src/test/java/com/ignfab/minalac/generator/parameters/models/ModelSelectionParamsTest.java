package com.ignfab.minalac.generator.parameters.models;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
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

        assertInstanceOf(ModelSelection.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));

        params.filter = TestingModelFilterParams.VALID;
        assertInstanceOf(ModelSelection.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }

    @Test
    public void testNarrowDown() {
        ModelSelectionParams params;
        ModelSelectionParams narrower;
        ModelFilterEmptyGeometryParams filter = new ModelFilterEmptyGeometryParams();

        // Narrow nothing with nothing: nothing
        params = new ModelSelectionParams();
        params.narrowDown(new ModelSelectionParams());

        assertNull(params.type);
        assertNull(params.filter);

        // Narrow with nothing: keep the same
        params = new ModelSelectionParams();
        params.type = "A";
        params.filter = filter;

        params.narrowDown(new ModelSelectionParams());

        assertEquals("A", params.type);
        assertEquals(filter, params.filter);

        // Narrow nothing with something: same as narrower
        params = new ModelSelectionParams();

        narrower = new ModelSelectionParams();
        narrower.type = "B";
        narrower.filter = filter;

        params.narrowDown(narrower);

        assertEquals("B", params.type);
        assertEquals(filter, params.filter);

        // Narrower brings type
        params = new ModelSelectionParams();
        params.filter = filter;

        narrower = new ModelSelectionParams();
        narrower.type = "C";

        params.narrowDown(narrower);

        assertEquals("C", params.type);
        assertEquals(filter, params.filter);

        // Same type -> same type
        params = new ModelSelectionParams();
        params.type = "D";
        params.filter = filter;

        narrower = new ModelSelectionParams();
        narrower.type = "D";

        params.narrowDown(narrower);

        assertEquals("D", params.type);
        assertEquals(filter, params.filter);

        // Narrower brings filter
        params = new ModelSelectionParams();
        params.type = "E";

        narrower = new ModelSelectionParams();
        narrower.filter = filter;

        params.narrowDown(narrower);

        assertEquals(filter, params.filter);

        // Combine filters
        params = new ModelSelectionParams();
        params.type = "F";
        params.filter = filter;

        narrower = new ModelSelectionParams();
        narrower.filter = new ModelFilterEmptyGeometryParams();

        params.narrowDown(narrower);

        assertInstanceOf(ModelFilterAndParams.class, params.filter);

        // Different types -> exception
        final ModelSelectionParams params2 = new ModelSelectionParams();
        params2.type = "G";

        final ModelSelectionParams narrower2 = new ModelSelectionParams();
        narrower2.type = "H";

        assertThrows(IllegalArgumentException.class, () -> params2.narrowDown(narrower2));
    }
}

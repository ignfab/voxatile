package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.parameters.models.values.FixedValueParams;
import com.ignfab.minalac.generator.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterHasValueParamsTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterHasValueParams(new FixedValueParams(1)));
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(new ModelFilterHasValueParams(TestingModelValueParams.VALID)::validate);
        assertThrows(IllegalArgumentException.class, new ModelFilterHasValueParams(TestingModelValueParams.INVALID)::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterHasValueParams params = new ModelFilterHasValueParams(new FixedValueParams(1));
        assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
    }
}

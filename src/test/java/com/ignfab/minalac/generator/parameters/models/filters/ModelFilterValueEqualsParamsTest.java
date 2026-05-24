package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnValue;
import com.ignfab.minalac.generator.parameters.models.values.FixedValueParams;
import com.ignfab.minalac.generator.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterValueEqualsParamsTest {
    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterValueEqualsParams(TestingModelValueParams.VALID, 1);
        assertDoesNotThrow(valid::validate);

        ModelFilterParams invalid = new ModelFilterValueEqualsParams(TestingModelValueParams.INVALID, 1);
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterValueEqualsParams(new FixedValueParams(1), 1);
        assertInstanceOf(ModelFilterOnValue.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }
}

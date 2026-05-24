package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnValue;
import com.ignfab.minalac.generator.parameters.models.values.MetadataValueParams;
import com.ignfab.minalac.generator.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterValueGreaterThanParamsTest {
    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterValueGreaterThanParams(TestingModelValueParams.VALID, 1.);
        assertDoesNotThrow(valid::validate);

        ModelFilterParams invalid = new ModelFilterValueGreaterThanParams(TestingModelValueParams.INVALID, 1.);
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterValueGreaterThanParams(new MetadataValueParams("height"), 1.);
        assertInstanceOf(ModelFilterOnValue.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }
}

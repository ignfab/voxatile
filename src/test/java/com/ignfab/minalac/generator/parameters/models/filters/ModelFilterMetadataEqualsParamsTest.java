package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterMetadataEqualsParamsTest {

    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterMetadataEqualsParams("a", 1);
        ModelFilterParams invalid = new ModelFilterMetadataEqualsParams("", 1);

        assertThrows(IllegalArgumentException.class, invalid::validate);
        assertDoesNotThrow(valid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterMetadataEqualsParams("a", 1);
        assertInstanceOf(ModelFilterOnMetadataValue.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }
}

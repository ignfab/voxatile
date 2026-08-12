package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.filters.ModelFilterOnMetadataValue;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterMetadataGreaterThanParamsTest {

    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterMetadataGreaterThanParams("a", 1.);
        ModelFilterParams invalid = new ModelFilterMetadataGreaterThanParams("", 1.);

        assertThrows(IllegalArgumentException.class, invalid::validate);
        assertDoesNotThrow(valid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterMetadataGreaterThanParams("a", 1.);
        assertInstanceOf(ModelFilterOnMetadataValue.class, assertDoesNotThrow(params::create));
    }
}

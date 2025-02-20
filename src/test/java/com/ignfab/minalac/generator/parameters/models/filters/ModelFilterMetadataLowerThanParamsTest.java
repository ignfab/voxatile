package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.filters.ModelFilterMetadataLowerThan;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterMetadataLowerThanParamsTest {

    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterMetadataLowerThanParams("a", 1.);
        ModelFilterParams invalid = new ModelFilterMetadataLowerThanParams("", 1.);

        assertThrows(IllegalArgumentException.class, invalid::validate);
        assertDoesNotThrow(valid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterMetadataLowerThanParams("a", 1.);
        assertInstanceOf(ModelFilterMetadataLowerThan.class, assertDoesNotThrow(params::create));
    }
}

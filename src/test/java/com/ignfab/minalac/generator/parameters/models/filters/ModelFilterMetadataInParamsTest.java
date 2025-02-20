package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.filters.ModelFilterMetadataIn;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterMetadataInParamsTest {

    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterMetadataInParams("a", List.of(1));
        ModelFilterParams invalid = new ModelFilterMetadataInParams("", List.of(1));

        assertThrows(IllegalArgumentException.class, invalid::validate);
        assertDoesNotThrow(valid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterMetadataInParams("a", List.of(1, 2));
        assertInstanceOf(ModelFilterMetadataIn.class, assertDoesNotThrow(params::create));
    }
}

package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplyShadingMinimapTaskParamsTest {

    @Test
    public void testValidate() {
        ApplyShadingMinimapTaskParams params;

        params = assertDoesNotThrow(() -> new ApplyShadingMinimapTaskParams(""));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> new ApplyShadingMinimapTaskParams(" \t   "));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> new ApplyShadingMinimapTaskParams("test"));
        assertDoesNotThrow(params::validate);

        params.shadowIntensity = -0.1;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.shadowIntensity = 1.1;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.sunAzimuth = -10;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.sunAzimuth = 370;
        assertThrows(IllegalArgumentException.class, params::validate);
    }
}

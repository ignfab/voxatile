package com.ignfab.minalac.generator.parameters;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class MinimapParamsTest {
    @Test
    void testDeserialize() {
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            MinimapParams.class,
            """
            size: 1234
            """
        ));
    }

    @Test
    void testValidate() {
        MinimapParams params = new MinimapParams();
        params.size = -8;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.size = 1234;
        assertDoesNotThrow(params::validate);
    }

    @Test
    void testCreate() {
        MinimapParams params = new MinimapParams();
        assertDoesNotThrow(() -> params.create(new WorldBBox2d(0, 0, 1, 1)));
    }
}

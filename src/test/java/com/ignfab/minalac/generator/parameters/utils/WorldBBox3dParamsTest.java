package com.ignfab.minalac.generator.parameters.utils;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.MismatchedInputException;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox3dParamsTest {
    @Test
    void testDeserialization() {
        WorldBBox3dParams params;
        WorldBBox3d box;

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(WorldBBox3dParams.class, "[]"));

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(WorldBBox3dParams.class, "[1, 2]"));

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(WorldBBox3dParams.class, "[1, 2, 3, 4]"));

        params = assertDeserialize("[1, 2, 3]");
        assertDoesNotThrow(params::validate);
        box = assertDoesNotThrow(params::create);
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), box);

        params = assertDeserialize("[1, 2, 3]");
        assertDoesNotThrow(params::validate);
        box = assertDoesNotThrow(params::create);
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), box);

        params = assertDeserialize("[1..1, 1..2, 1..3]");
        assertDoesNotThrow(params::validate);
        box = assertDoesNotThrow(params::create);
        assertEquals(new WorldBBox3d(1, 1, 1, 1, 2, 3), box);
    }

    private static WorldBBox3dParams assertDeserialize(String serialized) {
        return assertDoesNotThrow(() -> ParamsTester.deserialize(WorldBBox3dParams.class, serialized));
    }
}

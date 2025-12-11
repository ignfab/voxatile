package com.ignfab.minalac.generator.parameters.utils;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectReader;
import tools.jackson.dataformat.yaml.YAMLMapper;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox3dParamsTest {

    private ObjectReader reader = YAMLMapper.shared().readerFor(WorldBBox3dParams.class);

    @Test
    void testDeserialization() {
        WorldBBox3dParams params;
        WorldBBox3d box;

        params = assertDoesNotThrow(() -> reader.readValue("[]"));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> reader.readValue("[1, 2]"));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> reader.readValue("[1, 2, 3, 4]"));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> reader.readValue("[1, 2, 3]"));
        assertDoesNotThrow(params::validate);
        box = assertDoesNotThrow(params::create);
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), box);

        params = assertDoesNotThrow(() -> reader.readValue("[1, 2, 3]"));
        assertDoesNotThrow(params::validate);
        box = assertDoesNotThrow(params::create);
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), box);

        params = assertDoesNotThrow(() -> reader.readValue("[1..1, 1..2, 1..3]"));
        assertDoesNotThrow(params::validate);
        box = assertDoesNotThrow(params::create);
        assertEquals(new WorldBBox3d(1, 1, 1, 1, 2, 3), box);
    }
}

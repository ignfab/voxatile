package com.ignfab.minalac.generator.parameters.heightmaps;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ReadableHeightmapParamsDeserializerTest {

    @Test
    @DisplayName("Test heightmap deserialization using integer shortcut")
    public void testReadableHeightmapParamsDeserializerInteger() {
        ReadableHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(ReadableHeightmapParams.class, "700"));
        ConstantHeightmapParams chp = assertInstanceOf(ConstantHeightmapParams.class, params);
        assertEquals(700, chp.constant);
    }

    @Test
    @DisplayName("Test heightmap deserialization using string shortcut")
    public void testReadableHeightmapParamsDeserializerStored() {
        ReadableHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(ReadableHeightmapParams.class, "ground"));
        StoredHeightmapParams shp = assertInstanceOf(StoredHeightmapParams.class, params);
        assertEquals("ground", shp.name);
    }
}

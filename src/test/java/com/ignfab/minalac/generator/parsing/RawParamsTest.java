package com.ignfab.minalac.generator.parsing;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RawParamsTest {
    @Test
    public void testHeightmapParamsDefaultValueDeserialization() {
        // The "default" field deserialization involves a custom private method within RawParams.HeightmapParams.
        // This method, parseDefaultValue(), is responsible for mapping certain string value with int value (for example "minimal" with Integer.MIN_VALUE).
        // Because of that, this test is to ensure that the "default" field is properly deserialized.
        ObjectMapper mapper = new ObjectMapper();

        RawParams.HeightmapParams heightmapIntegerValue = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : 3
            }
            """,
            RawParams.HeightmapParams.class));
        assertEquals(3, heightmapIntegerValue.defaultValue);

        RawParams.HeightmapParams heightmapMinimal = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "minimal"
            }
            """,
            RawParams.HeightmapParams.class));
        assertEquals(Integer.MIN_VALUE, heightmapMinimal.defaultValue);

        RawParams.HeightmapParams heightmapMin = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "min"
            }
            """,
            RawParams.HeightmapParams.class));
        assertEquals(Integer.MIN_VALUE, heightmapMin.defaultValue);

        RawParams.HeightmapParams heightmapMaximal = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "maximal"
            }
            """,
            RawParams.HeightmapParams.class));
        assertEquals(Integer.MAX_VALUE, heightmapMaximal.defaultValue);

        RawParams.HeightmapParams heightmapMax = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "max"
            }
            """,
            RawParams.HeightmapParams.class));
        assertEquals(Integer.MAX_VALUE, heightmapMax.defaultValue);

        assertThrows(JacksonException.class, () -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "4foo"
            }
            """,
            RawParams.HeightmapParams.class));
    }
}

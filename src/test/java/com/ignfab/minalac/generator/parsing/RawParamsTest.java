package com.ignfab.minalac.generator.parsing;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RawParamsTest {
    @Test
    public void testHeightMapParamsDefaultValueDeserialization() {
        // The "default" field deserialization involves a custom private method within RawParams.HeightMapParams.
        // This method, parseDefaultValue(), is responsible for mapping certain string value with int value (for example "minimal" with Integer.MIN_VALUE).
        // Because of that, this test is to ensure that the "default" field is properly deserialized.
        ObjectMapper mapper = new ObjectMapper();

        RawParams.HeightMapParams heightMapIntegerValue = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : 3
            }
            """,
            RawParams.HeightMapParams.class));
        assertEquals(3, heightMapIntegerValue.defaultValue);

        RawParams.HeightMapParams heightMapMinimal = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "minimal"
            }
            """,
            RawParams.HeightMapParams.class));
        assertEquals(Integer.MIN_VALUE, heightMapMinimal.defaultValue);

        RawParams.HeightMapParams heightMapMin = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "min"
            }
            """,
            RawParams.HeightMapParams.class));
        assertEquals(Integer.MIN_VALUE, heightMapMin.defaultValue);

        RawParams.HeightMapParams heightMapMaximal = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "maximal"
            }
            """,
            RawParams.HeightMapParams.class));
        assertEquals(Integer.MAX_VALUE, heightMapMaximal.defaultValue);

        RawParams.HeightMapParams heightMapMax = assertDoesNotThrow(() -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "max"
            }
            """,
            RawParams.HeightMapParams.class));
        assertEquals(Integer.MAX_VALUE, heightMapMax.defaultValue);

        assertThrows(JacksonException.class, () -> mapper.readValue(
            """
            {
              "name" : "test",
              "default" : "4foo"
            }
            """,
            RawParams.HeightMapParams.class));
    }
}

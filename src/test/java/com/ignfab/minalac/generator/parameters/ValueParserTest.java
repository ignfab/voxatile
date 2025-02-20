package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueParserTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ValueParser<>(String.class, Object::toString));
    }

    @Test
    public void testGet() {
        assertEquals(ValueParser.BOOLEAN, assertDoesNotThrow(() -> ValueParser.get("boolean")));
        assertEquals(ValueParser.DOUBLE, assertDoesNotThrow(() -> ValueParser.get("decimal")));
        assertEquals(ValueParser.INTEGER, assertDoesNotThrow(() -> ValueParser.get("integer")));
        assertEquals(ValueParser.STRING, assertDoesNotThrow(() -> ValueParser.get("text")));

        assertThrows(IllegalArgumentException.class, () -> ValueParser.get(""));
        assertThrows(IllegalArgumentException.class, () -> ValueParser.get("toto"));
    }

    @Test
    public void testRegister() {
        ValueParser<?> parser = new ValueParser<>(String.class, Object::toString);
        assertDoesNotThrow(() -> parser.register("dummy"));
        assertThrows(IllegalArgumentException.class, () -> parser.register("dummy"));
        assertEquals(parser, assertDoesNotThrow(() -> ValueParser.get("dummy")));
    }

    @Test
    public void testIntegerParser() {
        Integer result;

        result = assertDoesNotThrow(() -> ValueParser.INTEGER.parse("1234567890"));
        assertEquals(1234567890, result);

        result = assertDoesNotThrow(() -> ValueParser.INTEGER.parse("-3"));
        assertEquals(-3, result);

        assertDoesNotThrow(() -> ValueParser.INTEGER.parse(12345));

        assertThrows(NumberFormatException.class, () -> ValueParser.INTEGER.parse(12345.6789));
        assertThrows(NumberFormatException.class, () -> ValueParser.INTEGER.parse("12345.67890"));
    }

    @Test
    public void testDoubleParser() {
        Double result;

        result = assertDoesNotThrow(() -> ValueParser.DOUBLE.parse("12345.67890"));
        assertEquals(12345.6789, result);

        result = assertDoesNotThrow(() -> ValueParser.DOUBLE.parse("-9"));
        assertEquals(-9, result);

        result = assertDoesNotThrow(() -> ValueParser.DOUBLE.parse(1234567890));
        assertEquals(1234567890, result);

        assertThrows(NumberFormatException.class, () -> ValueParser.DOUBLE.parse("toto"));
    }

    @Test
    public void testStringParser() {
        // Assume converting "dummy" to a string works with "1", "8.9", or an object, so no further testing is needed.
        assertEquals("dummy", ValueParser.STRING.parse("dummy"));
    }

    @Test
    public void testBooleanParser() {
        assertTrue(assertDoesNotThrow(() -> ValueParser.BOOLEAN.parse("true")));
        assertTrue(assertDoesNotThrow(() -> ValueParser.BOOLEAN.parse(true)));
        assertFalse(assertDoesNotThrow(() -> ValueParser.BOOLEAN.parse("false")));
        assertFalse(assertDoesNotThrow(() -> ValueParser.BOOLEAN.parse(false)));

        assertThrows(IllegalArgumentException.class, () -> ValueParser.BOOLEAN.parse("yes"));
        assertThrows(IllegalArgumentException.class, () -> ValueParser.BOOLEAN.parse(1.));
    }

    @Test
    public void testDeserializer() {
        ValueParser<?> params = assertDoesNotThrow(() -> ParamsTester.deserialize(ValueParser.class, "integer"));
        assertEquals(Integer.class, params.type());

        assertThrows(JsonMappingException.class, () -> ParamsTester.deserialize(ValueParser.class, "yolo"));
    }
}

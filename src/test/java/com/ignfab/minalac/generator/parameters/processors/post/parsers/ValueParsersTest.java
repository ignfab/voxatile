package com.ignfab.minalac.generator.parameters.processors.post.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class ValueParsersTest {
    @Test
    public void testValidate() {
        assertDoesNotThrow(() -> ValueParsers.validate("integer"));

        assertThrows(IllegalArgumentException.class, () -> ValueParsers.validate(""));
        assertThrows(IllegalArgumentException.class, () -> ValueParsers.validate("toto"));
    }

    @Test
    public void testAddParser() {
        assertDoesNotThrow(() -> ValueParsers.addParser("dummy", String.class, Object::toString));
        assertThrows(IllegalArgumentException.class, () -> ValueParsers.addParser("dummy", String.class, Object::toString));
    }

    @Test
    public void testIntegerParser() {
        Integer result;
        Function<Object, ?> parser = ValueParsers.get("integer").parser();

        result = (Integer) assertDoesNotThrow(() -> parser.apply("1234567890"));
        assertEquals(1234567890, result);

        result = (Integer) assertDoesNotThrow(() -> parser.apply("-3"));
        assertEquals(-3, result);

        assertDoesNotThrow(() -> parser.apply(12345));

        assertThrows(NumberFormatException.class, () -> parser.apply(12345.6789));
        assertThrows(NumberFormatException.class, () -> parser.apply("12345.67890"));
    }

    @Test
    public void testDecimalParser() {
        Double result;
        Function<Object, ?> parser = ValueParsers.get("decimal").parser();

        result = (Double) assertDoesNotThrow(() -> parser.apply("12345.67890"));
        assertEquals(12345.6789, result);

        result = (Double) assertDoesNotThrow(() -> parser.apply("-9"));
        assertEquals(-9, result);

        result = (Double) assertDoesNotThrow(() -> parser.apply(1234567890));
        assertEquals(1234567890, result);

        assertThrows(NumberFormatException.class, () -> parser.apply("toto"));
    }

    @Test
    public void testTextParser() {
        // Assume converting "dummy" to a string works with "1", "8.9", or an object, so no further testing is needed.
        assertEquals("dummy", (String) ValueParsers.get("text").parser().apply("dummy"));
    }

    @Test
    public void testBooleanParser() {
        Function<Object, ?> parser = ValueParsers.get("boolean").parser();

        assertTrue((Boolean) assertDoesNotThrow(() -> parser.apply("true")));
        assertTrue((Boolean) assertDoesNotThrow(() -> parser.apply(true)));
        assertFalse((Boolean) assertDoesNotThrow(() -> parser.apply("false")));
        assertFalse((Boolean) assertDoesNotThrow(() -> parser.apply(false)));

        assertThrows(IllegalArgumentException.class, () -> parser.apply("yes"));
        assertThrows(IllegalArgumentException.class, () -> parser.apply(1.));
    }
}

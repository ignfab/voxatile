package com.ignfab.minalac.generator.parameters;

import java.awt.Color;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

public class ColorDeserializerTest {

    @Test
    public void testDeserialize() {
        Color color = assertDoesNotThrow(() -> ParamsTester.deserialize(Color.class, "\"#FF0000\""));
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());

        color = assertDoesNotThrow(() -> ParamsTester.deserialize(Color.class, "[0, 255, 0]"));
        assertEquals(0, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(0, color.getBlue());

        color = assertDoesNotThrow(() -> ParamsTester.deserialize(Color.class, "[0, 0, 255, 128]"));
        assertEquals(255, color.getBlue());
        assertEquals(128, color.getAlpha());

        assertThrows(IllegalArgumentException.class, () ->
            ParamsTester.deserialize(Color.class, "[0, 0, -1]")
        );

        assertThrows(IllegalArgumentException.class, () ->
            ParamsTester.deserialize(Color.class, "[256, 0, 0]")
        );

        assertThrows(InvalidFormatException.class, () ->
            ParamsTester.deserialize(Color.class, "\"not-a-color\"")
        );

        assertThrows(MismatchedInputException.class, () ->
            ParamsTester.deserialize(Color.class, "[255, 255]")
        );

        assertThrows(MismatchedInputException.class, () ->
            ParamsTester.deserialize(Color.class, "{ r: 255, g: 0, b: 0 }")
        );
    }
}

package com.ignfab.minalac.generator.utils.axis.mappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityIndexMapperTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new IdentityAxisMapper(-1));
    }

    @Test
    public void testMap() {
        AxisMapper empty = assertDoesNotThrow(() -> new IdentityAxisMapper(0));
        assertThrows(IndexOutOfBoundsException.class, () -> empty.map(0));

        AxisMapper notEmpty = assertDoesNotThrow(() -> new IdentityAxisMapper(3));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.map(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.map(3));

        assertEquals(new AxisMapper.Mapped(0, 0), notEmpty.map(0));
        assertEquals(new AxisMapper.Mapped(0, 1), notEmpty.map(1));
        assertEquals(new AxisMapper.Mapped(0, 2), notEmpty.map(2));
    }

    @Test
    public void testIntervals() {
        int[] empty = assertDoesNotThrow(() -> (new IdentityAxisMapper(0).intervals()));
        assertEquals(0, empty.length);

        int[] notEmpty = assertDoesNotThrow(() -> (new IdentityAxisMapper(7).intervals()));
        assertEquals(1, notEmpty.length);
        assertEquals(7, notEmpty[0]);
    }

    @Test
    public void testSize() {
        AxisMapper empty = assertDoesNotThrow(() -> new IdentityAxisMapper(0));
        assertEquals(0, empty.size());

        AxisMapper notEmpty = assertDoesNotThrow(() -> new IdentityAxisMapper(37));
        assertEquals(37, notEmpty.size());
    }
}

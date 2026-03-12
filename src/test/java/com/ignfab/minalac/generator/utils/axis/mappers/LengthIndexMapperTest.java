package com.ignfab.minalac.generator.utils.axis.mappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LengthIndexMapperTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new SizesAxisMapper(2, 1, -5));
        assertThrows(IllegalArgumentException.class, () -> new SizesAxisMapper(-2, 1, 5));
        assertDoesNotThrow(() -> new SizesAxisMapper());
        assertDoesNotThrow(() -> new SizesAxisMapper(1, 2, 0));
        assertDoesNotThrow(() -> new SizesAxisMapper(1, 2, 7));
    }

    @Test
    public void testMap() {
        AxisMapper empty = assertDoesNotThrow(() -> new SizesAxisMapper(0));
        assertThrows(IndexOutOfBoundsException.class, () -> empty.map(0));

        AxisMapper notEmpty = assertDoesNotThrow(() -> new SizesAxisMapper(3));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.map(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> notEmpty.map(3));

        assertEquals(new AxisMapper.Mapped(0, 0), notEmpty.map(0));
        assertEquals(new AxisMapper.Mapped(0, 1), notEmpty.map(1));
        assertEquals(new AxisMapper.Mapped(0, 2), notEmpty.map(2));

        AxisMapper multiple = assertDoesNotThrow(() -> new SizesAxisMapper(3, 0, 1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> multiple.map(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> multiple.map(6));

        // First interval (length 3)
        assertEquals(new AxisMapper.Mapped(0, 0), multiple.map(0));
        assertEquals(new AxisMapper.Mapped(0, 1), multiple.map(1));
        assertEquals(new AxisMapper.Mapped(0, 2), multiple.map(2));

        // Third interval (length 1)
        assertEquals(new AxisMapper.Mapped(2, 0), multiple.map(3));

        // Fourth interval (length 0)
        assertEquals(new AxisMapper.Mapped(3, 0), multiple.map(4));
        assertEquals(new AxisMapper.Mapped(3, 1), multiple.map(5));
    }

    @Test
    public void testIntervals() {
        assertArrayEquals(new int[] {}, new SizesAxisMapper().intervals());
        assertArrayEquals(new int[] { 3, 1, 2 }, new SizesAxisMapper(3, 1, 2).intervals());
        assertArrayEquals(new int[] { 0, 1, 2 }, new SizesAxisMapper(0, 1, 2).intervals());
    }

    @Test
    public void testSize() {
        AxisMapper empty = assertDoesNotThrow(() -> new SizesAxisMapper(0));
        assertEquals(0, empty.size());

        AxisMapper notEmpty = assertDoesNotThrow(() -> new SizesAxisMapper(3, 1, 5));
        assertEquals(9, notEmpty.size());
    }
}

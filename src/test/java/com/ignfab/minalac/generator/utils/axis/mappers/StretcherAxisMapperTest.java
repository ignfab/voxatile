package com.ignfab.minalac.generator.utils.axis.mappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StretcherAxisMapperTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, 0, 2, -1), "negative length");
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, 0, -1, 1), "negative base length");
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, 0, 3, 1), "Squeezed more than once");
        assertThrows(IllegalArgumentException.class, () -> new StretcherIndexMapper(0, 4, 2, 2), "stretch out of size");
    }

    @Test
    public void testMap() {
        AxisMapper same = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 1, 3, 3)));
        assertEquals(new AxisMapper.Mapped(0, 0), same.map(0));
        assertEquals(new AxisMapper.Mapped(0, 1), same.map(1));
        assertEquals(new AxisMapper.Mapped(0, 2), same.map(2));

        AxisMapper stretched = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 1, 3, 5)));
        assertEquals(new AxisMapper.Mapped(0, 0), stretched.map(0));
        assertEquals(new AxisMapper.Mapped(0, 1), stretched.map(1));
        assertEquals(new AxisMapper.Mapped(0, 1), stretched.map(2));
        assertEquals(new AxisMapper.Mapped(0, 1), stretched.map(3));
        assertEquals(new AxisMapper.Mapped(0, 2), stretched.map(4));

        AxisMapper squeezedFirst = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 3, 2)));
        assertEquals(new AxisMapper.Mapped(0, 1), squeezedFirst.map(0));
        assertEquals(new AxisMapper.Mapped(0, 2), squeezedFirst.map(1));

        AxisMapper squeezedSecond = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 1, 3, 2)));
        assertEquals(new AxisMapper.Mapped(0, 0), squeezedSecond.map(0));
        assertEquals(new AxisMapper.Mapped(0, 2), squeezedSecond.map(1));

        AxisMapper stretchedWithOffset = assertDoesNotThrow(() -> (new StretcherIndexMapper(-2, -1, 3, 5)));
        assertEquals(new AxisMapper.Mapped(0, -2), stretchedWithOffset.map(-2));
        assertEquals(new AxisMapper.Mapped(0, -1), stretchedWithOffset.map(-1));
        assertEquals(new AxisMapper.Mapped(0, -1), stretchedWithOffset.map(0));
        assertEquals(new AxisMapper.Mapped(0, -1), stretchedWithOffset.map(1));
        assertEquals(new AxisMapper.Mapped(0, 0), stretchedWithOffset.map(2));

        AxisMapper squeezedSecondWithOffset = assertDoesNotThrow(() -> (new StretcherIndexMapper(4, 5, 3, 2)));
        assertEquals(new AxisMapper.Mapped(0, 4), squeezedSecondWithOffset.map(4));
        assertEquals(new AxisMapper.Mapped(0, 6), squeezedSecondWithOffset.map(5));

        AxisMapper squeezedFirstWithOffset = assertDoesNotThrow(() -> (new StretcherIndexMapper(4, 4, 3, 2)));
        assertEquals(new AxisMapper.Mapped(0, 5), squeezedFirstWithOffset.map(4));
        assertEquals(new AxisMapper.Mapped(0, 6), squeezedFirstWithOffset.map(5));

        AxisMapper zeroSize = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> zeroSize.map(0));
    }

    @Test
    public void testIntervalsAndSize() {
        AxisMapper same = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 3, 3)));
        assertArrayEquals(new int[] { 3 }, same.intervals());
        assertEquals(3, same.size());

        AxisMapper stretched = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 3, 5)));
        assertArrayEquals(new int[] { 3 }, stretched.intervals());
        assertEquals(5, stretched.size());

        AxisMapper squeezed = assertDoesNotThrow(() -> (new StretcherIndexMapper(0, 0, 3, 2)));
        assertArrayEquals(new int[] { 3 }, squeezed.intervals());
        assertEquals(2, squeezed.size());
    }
}

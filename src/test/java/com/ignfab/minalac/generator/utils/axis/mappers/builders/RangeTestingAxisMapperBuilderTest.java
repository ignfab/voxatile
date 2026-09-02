package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RangeTestingAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(IllegalArgumentException.class, () -> new RangeTestingAxisMapperBuilder(5, 2, 0));
        assertDoesNotThrow(() -> new RangeTestingAxisMapperBuilder(2, 2, 0));
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(0, new RangeTestingAxisMapperBuilder(0).maxSizeUnder(1));
        assertEquals(1, new RangeTestingAxisMapperBuilder(0, 2).maxSizeUnder(1));

        assertEquals(-1, new RangeTestingAxisMapperBuilder(2, 5).maxSizeUnder(1));

        assertEquals(2, new RangeTestingAxisMapperBuilder(2, 5).maxSizeUnder(2));
        assertEquals(5, new RangeTestingAxisMapperBuilder(2, 5).maxSizeUnder(5));
        assertEquals(3, new RangeTestingAxisMapperBuilder(2, 5).maxSizeUnder(3));
        assertEquals(5, new RangeTestingAxisMapperBuilder(2, 5).maxSizeUnder(6));

        assertEquals(2, new RangeTestingAxisMapperBuilder(2).maxSizeUnder(3));
        assertEquals(2, new RangeTestingAxisMapperBuilder(2).maxSizeUnder(2));
        assertEquals(-1, new RangeTestingAxisMapperBuilder(2).maxSizeUnder(1));

    }

    @Test
    public void testMinimumSize() {
        assertEquals(2, new RangeTestingAxisMapperBuilder(2, 5).minimumSize());
        assertEquals(2, new RangeTestingAxisMapperBuilder(2).minimumSize());
    }
}

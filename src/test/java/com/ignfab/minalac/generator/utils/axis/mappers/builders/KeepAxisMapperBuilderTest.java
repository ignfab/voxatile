package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeepAxisMapperBuilderTest {

    @Test
    public void testMaxSizeUnder() {
        AxisMapperBuilder fixed = assertDoesNotThrow(() -> new KeepAxisMapperBuilder(
            new RangeTestingAxisMapperBuilder(2),
            new RangeTestingAxisMapperBuilder(7),
            new RangeTestingAxisMapperBuilder(5)
        ));
        // Any size greater or equal to min is fine
        assertEquals(7, fixed.maxSizeUnder(7));
        assertEquals(8, fixed.maxSizeUnder(8));
        assertEquals(25, fixed.maxSizeUnder(25));
        assertEquals(-1, fixed.maxSizeUnder(6));
    }

    @Test
    public void testMinimumSize() {
        AxisMapperBuilder fixed = assertDoesNotThrow(() -> new KeepAxisMapperBuilder(
            new RangeTestingAxisMapperBuilder(2),
            new RangeTestingAxisMapperBuilder(7),
            new RangeTestingAxisMapperBuilder(5)
        ));
        assertEquals(7, fixed.minimumSize());

        AxisMapperBuilder moving = assertDoesNotThrow(() -> new KeepAxisMapperBuilder(
            new RangeTestingAxisMapperBuilder(2, 12),
            new RangeTestingAxisMapperBuilder(7, 9),
            new RangeTestingAxisMapperBuilder(5, 10)
        ));
        assertEquals(7, moving.minimumSize());
    }

    @Test
    public void testOrigin() {
        AxisMapperBuilder different = assertDoesNotThrow(() -> new KeepAxisMapperBuilder(
            new RangeTestingAxisMapperBuilder(1, 2, 5),
            new RangeTestingAxisMapperBuilder(1, 2, 2),
            new RangeTestingAxisMapperBuilder(1, 2, -7)
        ));
        assertEquals(-7, different.origin());

        AxisMapperBuilder same = assertDoesNotThrow(() -> new KeepAxisMapperBuilder(
            new RangeTestingAxisMapperBuilder(1, 2, 5),
            new RangeTestingAxisMapperBuilder(1, 2, 5)
        ));
        assertEquals(5, same.origin());
    }
}

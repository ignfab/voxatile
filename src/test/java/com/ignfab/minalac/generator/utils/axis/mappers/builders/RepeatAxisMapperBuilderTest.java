package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnbuildableLayoutException.class, () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 1, Integer.MAX_VALUE).build(5));
        assertThrows(UnbuildableLayoutException.class, () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 1, Integer.MAX_VALUE).build(7));
    }

    @Test
    public void testMaxSizeFittingUnder() {
        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 2, Integer.MAX_VALUE).maxSizeFittingUnder(6))
        );


        assertEquals(-1, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 2, Integer.MAX_VALUE).maxSizeFittingUnder(5))
        );

        assertThrows(
            UnbuildableLayoutException.class,
            () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(0), 1, Integer.MAX_VALUE).maxSizeFittingUnder(0)
        );
        assertThrows(
            UnbuildableLayoutException.class,
            () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(0, 2), 1, Integer.MAX_VALUE).maxSizeFittingUnder(5)
        );

        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3, 4), 2, Integer.MAX_VALUE).maxSizeFittingUnder(6))
        );
        assertEquals(10, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3, 4), 2, Integer.MAX_VALUE).maxSizeFittingUnder(10))
        );

        // Unresizable underlying structure
        // Max under 25 is 2 * 7 = 14
        assertEquals(14, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(7), 2, 2).maxSizeFittingUnder(25))
        );
        // Max under 14 is 2 * 7 = 14
        assertEquals(14, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(7), 2, 2).maxSizeFittingUnder(14))
        );

        // Resizable underlying structure (Can take any size from 2 to 8)
        // Max under 30 is max([2, 8]) * maxOccurences (3 here) -> 8 * 3 = 24
        assertEquals(24, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(2, 8), 2, 3).maxSizeFittingUnder(30))
        );

        // Three 8
        assertEquals(24, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new AllowlistTestingAxisMapperBuilder(new int[] { 6, 8 }), 2, 3).maxSizeFittingUnder(25))
        );
        // Two 8 and one 6
        assertEquals(22, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new AllowlistTestingAxisMapperBuilder(new int[] { 6, 8 }), 2, 3).maxSizeFittingUnder(23))
        );
    }

    @Test
    public void testMinimumSize() {
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 0, Integer.MAX_VALUE).minimumSize())
        );
        assertEquals(3, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 1, Integer.MAX_VALUE).minimumSize())
        );
        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new RangeTestingAxisMapperBuilder(3), 2, Integer.MAX_VALUE).minimumSize())
        );
    }
}

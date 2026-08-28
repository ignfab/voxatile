package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

import static org.junit.jupiter.api.Assertions.*;

public class AdjustAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(
            UnbuildableException.class,
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 5, 0),
                new RangeTestingAxisMapperBuilder(1, 5, 1)
            ),
            "Different origin"
        );

        assertThrows(
            UnbuildableException.class,
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 5),
                new RangeTestingAxisMapperBuilder(6, 9)
            ),
            "No min sizes"
        );

        assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(0),
                new RangeTestingAxisMapperBuilder(0, 9)
            )
        );

        assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 6),
                new RangeTestingAxisMapperBuilder(4, 9)
            )
        );
    }

    @Test
    public void testMaxSizeUnder() {
        assertThrows(
            UnbuildableException.class,
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 5),
                new RangeTestingAxisMapperBuilder(7, 8))
        );

        AxisMapperBuilder builder = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 7),
                new RangeTestingAxisMapperBuilder(5, 10))
        );
        assertEquals(7, builder.maxSizeUnder(10));
        assertEquals(7, builder.maxSizeUnder(7));
        assertEquals(6, builder.maxSizeUnder(6));
        assertEquals(5, builder.maxSizeUnder(5));
        assertEquals(-1, builder.maxSizeUnder(4));

        AxisMapperBuilder canBeZero = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(0, 7),
                new RangeTestingAxisMapperBuilder(0, 10))
        );
        assertEquals(7, canBeZero.maxSizeUnder(10));
        assertEquals(0, canBeZero.maxSizeUnder(0));
    }

    @Test
    public void testMinimumSize() {
        AxisMapperBuilder nonZeroMinSize = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 7),
                new RangeTestingAxisMapperBuilder(5, 10),
                new RangeTestingAxisMapperBuilder(4, 6))
        );
        assertEquals(5, nonZeroMinSize.minimumSize());

        AxisMapperBuilder zeroMinSize = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(0, 7),
                new RangeTestingAxisMapperBuilder(0, 10)
            ));
        assertEquals(0, zeroMinSize.minimumSize());
    }

    @Test
    public void testOrigin() {
        AxisMapperBuilder builder = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 7, -3),
                new RangeTestingAxisMapperBuilder(5, 10, -3))
        );
        assertEquals(-3, builder.origin());
    }
}

package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriorityRepartitionAxisMapperBuilderTest {

    @Test
    public void testMaxSizeUnderBuild() {
        AxisMapperBuilder minSizeError = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new TestingAxisMapperBuilder(2, 5), new TestingAxisMapperBuilder(0), new TestingAxisMapperBuilder(6, 7)},
                new int[] {0, 0, 0}
            )
        );
        assertEquals(-1, minSizeError.maxSizeUnder(7));
        assertThrows(UnbuildableException.class, () -> minSizeError.build(7));

        AxisMapperBuilder minZero = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new TestingAxisMapperBuilder(0, 4), new TestingAxisMapperBuilder(0, 4)},
                new int[] {0, 0}
            )
        );

        assertEquals(5, minZero.maxSizeUnder(5));
        AxisMapper withMinZero = assertDoesNotThrow(() -> minZero.build(5));
        // Unordered tests as there is no control on which one will have the most
        assertBrowsesAllOnce(List.of(2, 3), Arrays.stream(withMinZero.intervals()).iterator());

        // Same priority
        AxisMapperBuilder samePrio = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new TestingAxisMapperBuilder(2), new TestingAxisMapperBuilder(3)},
                new int[] {0, 0}
            )
        );
        assertEquals(5, samePrio.maxSizeUnder(6));
        AxisMapper withSamePrio = assertDoesNotThrow(() -> samePrio.build(5));
        assertArrayEquals(new int[] {2, 3}, withSamePrio.intervals());

        // Priority should take all possible after distribution of min
        AxisMapperBuilder builderWithOnePrio = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new TestingAxisMapperBuilder(0, 8), new TestingAxisMapperBuilder(2, 4)},
                new int[] {1, 0}
            )
        );

        // No remainder for first one
        assertEquals(2, builderWithOnePrio.maxSizeUnder(2));
        AxisMapper withSize2 = assertDoesNotThrow(() -> builderWithOnePrio.build(2));
        assertArrayEquals(new int[] {0, 2}, withSize2.intervals());

        // First takes all remainder
        assertEquals(8, builderWithOnePrio.maxSizeUnder(8));
        AxisMapper withSize8 = assertDoesNotThrow(() -> builderWithOnePrio.build(8));
        assertArrayEquals(new int[] {6, 2}, withSize8.intervals());

        assertEquals(10, builderWithOnePrio.maxSizeUnder(10));
        AxisMapper withSize10 = assertDoesNotThrow(() -> builderWithOnePrio.build(10));
        assertArrayEquals(new int[] {8, 2}, withSize10.intervals());

        // First has taken all available remainder the rest goes for the second one
        assertEquals(11, builderWithOnePrio.maxSizeUnder(11));
        AxisMapper withSize11 = assertDoesNotThrow(() -> builderWithOnePrio.build(11));
        assertArrayEquals(new int[] {8, 3}, withSize11.intervals());
    }

    @Test
    public void testMinimumSize() {
        assertEquals(
            8,
            assertDoesNotThrow(
                () ->  new PriorityRepartitionAxisMapperBuilder(
                    new AxisMapperBuilder[] {new TestingAxisMapperBuilder(2, 5), new TestingAxisMapperBuilder(0), new TestingAxisMapperBuilder(6, 7)},
                    new int[] {0, 1, 0}
                )
            ).minimumSize()
        );

        assertEquals(
            0,
            assertDoesNotThrow(
                () ->  new PriorityRepartitionAxisMapperBuilder(
                    new AxisMapperBuilder[] {new TestingAxisMapperBuilder(0, 5), new TestingAxisMapperBuilder(0), new TestingAxisMapperBuilder(0, 4)},
                    new int[] {1, 0, 0}
                )
            ).minimumSize()
        );
    }
}

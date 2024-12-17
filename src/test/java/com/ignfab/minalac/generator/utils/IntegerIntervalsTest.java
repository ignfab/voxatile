package com.ignfab.minalac.generator.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.iterator.IteratorTester;

public class IntegerIntervalsTest {
    @Test
    void testAdd() {
        IntegerIntervals intervals = new IntegerIntervals();
        assertDoesNotThrow(() -> intervals.add(new IntegerInterval(4, 5)));
        assertDoesNotThrow(() -> intervals.add(4, 5));
        assertThrows(IllegalArgumentException.class, () -> intervals.add(new IntegerInterval(3, 2)));
    }

    @Test
    void testIterator() {
        IntegerIntervals intervals = new IntegerIntervals();
        intervals.add(new IntegerInterval(4, 5));
        intervals.add(2, 3);

        IteratorTester.assertBrowsesAllOnce(Arrays.asList(
                new IntegerInterval(2, 3),
                new IntegerInterval(4, 5)
            ),
            intervals
        );
    }

    @Test
    void testMerged() {
        IntegerIntervals intervals = new IntegerIntervals();
        intervals.add(1, 2);
        intervals.add(0, 3);
        intervals.add(5, 6);
        intervals.add(6, 7);
        intervals.add(8, 8);
        intervals = assertDoesNotThrow(intervals::merged);
        IteratorTester.assertBrowsesAllOnce(Arrays.asList(
                new IntegerInterval(0, 3),
                new IntegerInterval(5, 7),
                new IntegerInterval(8, 8) // Not clear whether "touching" intervals should merge.
            ),
            intervals
        );
    }
}

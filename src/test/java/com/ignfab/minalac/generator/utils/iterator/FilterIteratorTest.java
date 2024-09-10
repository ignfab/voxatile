package com.ignfab.minalac.generator.utils.iterator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.Collections;

public class FilterIteratorTest {
    @Test
    void testIterator() {
        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new Integer[] { 1, 3, 5, 7, 9 }),
            new FilterIterator<>(
                Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }),
                item -> item % 2 != 0)
        );

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new Integer[] { 0, 2, 4, 6, 8, 10 }),
            new FilterIterator<>(
                Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).iterator(),
                item -> item % 2 == 0)
        );

        assertFalse(new FilterIterator<>(
                Arrays.asList(new String[] { "a", "b", "c" }),
                item -> false).hasNext()
        );

        assertFalse(new FilterIterator<>(
            Collections.emptyIterator(),
            item -> true).hasNext()
        );
    }
}

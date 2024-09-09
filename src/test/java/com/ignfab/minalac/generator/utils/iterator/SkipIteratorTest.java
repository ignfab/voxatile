package com.ignfab.minalac.generator.utils.iterator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.Collections;

public class SkipIteratorTest {
    @Test
    void testIterator() {
        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new Integer[] { 1, 3, 5, 7, 9 }),
            new SkipIterator<Integer>(
                Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }),
                item -> item % 2 == 0)
        );

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new Integer[] { 1, 3, 5, 7, 9 }),
            new SkipIterator<Integer>(
                Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).iterator(),
                item -> item % 2 == 0)
        );

        assertFalse(new SkipIterator<String>(
                Arrays.asList(new String[] { "a", "b", "c" }),
                item -> true).hasNext()
        );

        assertFalse(new SkipIterator<String>(
            Collections.emptyIterator(),
            item -> false).hasNext()
        );
    }
}

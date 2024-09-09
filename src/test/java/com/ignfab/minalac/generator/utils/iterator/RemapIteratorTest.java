package com.ignfab.minalac.generator.utils.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class RemapIteratorTest {
    @Test
    void testIterator() {
        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new Integer[] { 3, 5, 5, 6, 6, 6, 7 }),
            new RemapIterator<String, Integer>(
                Arrays.asList(new String[] {
                    "Doc", "Grumpy", "Happy", "Sleepy", "Bashful", "Sneezy", "Dopey"
                }),
                String::length
        ));

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            new RemapIterator<String, Integer>(Collections.emptyList(), String::length
        ));
    }
}

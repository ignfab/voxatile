package com.ignfab.minalac.generator.utils.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class ArrayIteratorTest {
    @Test
    void testIterator() {
        String[] array = { "aa", "bb", "cc", "dd", "ee" };
        IteratorTester.assertBrowsesAllOnce(Arrays.asList(array), new ArrayIterator<String>(array));

        IteratorTester.assertBrowsesAllOnce(Collections.emptyList(), new ArrayIterator<String>(new String[] {}));
    }
}

package com.ignfab.minalac.generator.utils.iterator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SingletonIteratorTest {
    @Test
    void testIterator() {
        IteratorTester.assertBrowsesAllOnce(Arrays.asList(new String[] { "toto" }),
            new SingletonIterator<String>("toto"));
    }
}

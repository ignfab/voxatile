package com.ignfab.minalac.generator.utils.iterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

public class IteratorTesterTest {

    @Test
    void testAssertBrowseAllOnce() {
        List<String> expected = Arrays.asList("a", "c", "b");
        List<String> iterable = Arrays.asList("a", "b", "c");

        // Should be the same in different orders, iterable or iterator
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAllOnce(expected, iterable.iterator());
        });

        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAllOnce(expected, iterable);
        });

        // Missing with distinct values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(3, 4, 5, 6),
                Arrays.asList(6, 5, 3));
        });

        // Missing with same values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(3, 3, 5, 6),
                Arrays.asList(6, 5, 3));
        });

        // Unexpected with distinct values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(1.1, 2.2, 3.3),
                Arrays.asList(3.3, 2.2, 2.1, 1.1));
        });

        // Unexpected with same values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(1.1, 2.2, 3.3),
                Arrays.asList(1.1, 2.2, 3.3, 3.3));
        });

        // Got something different but with the same count
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList("X", "Y", "Z", "T"),
                Arrays.asList("X", "Z", "Y", "U"));
        });

        // Same count, same values, but different
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList("X", "Y", "Z", "Z"),
                Arrays.asList("X", "Z", "Y", "Y"));
        });

        // Test empty stuff
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAllOnce(
                Collections.emptyList(),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList("Yolo"),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Collections.emptyList(),
                Arrays.asList("Bruh"));
        });
    }

    @Test
    void testAssertBrowseAll() {
        List<String> expected = Arrays.asList("a", "c", "b");
        List<String> iterable = Arrays.asList("a", "b", "c");

        // Should be the same in different orders, iterable or iterator
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAll(expected, iterable.iterator());
        });

        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAll(expected, iterable);
        });

        // Missing with distinct values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(3, 4, 5, 6),
                Arrays.asList(6, 5, 3));
        });

        // Missing with same values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(3, 3, 5, 6),
                Arrays.asList(6, 5, 3));
        });

        // Unexpected with distinct values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(1.1, 2.2, 3.3),
                Arrays.asList(3.3, 2.2, 2.1, 1.1));
        });

        // Browsing twice the same value
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(1.1, 2.2, 3.3),
                Arrays.asList(1.1, 2.2, 3.3, 3.3));
        });

        // Got something different but with the same count
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList("X", "Y", "Z", "T"),
                Arrays.asList("X", "Z", "Y", "U"));
        });

        // Expected twice the same item
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList("X", "Y", "Z", "Z"),
                Arrays.asList("X", "Z", "Y", "Y"));
        });

        // Test empty stuff
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAll(
                Collections.emptyList(),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList("Yolo"),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Collections.emptyList(),
                Arrays.asList("Bruh"));
        });
    }
}

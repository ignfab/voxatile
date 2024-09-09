package com.ignfab.minalac.generator.utils.iterator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class IteratorTesterTest {

    @Test
    void testAssertBrowseAllOnce() {
        List<String> expected = Arrays.asList(new String[] { "a", "c", "b" });
        List<String> iterable = Arrays.asList(new String[] { "a", "b", "c" });

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
                Arrays.asList(new Integer[] { 3, 4, 5, 6 }),
                Arrays.asList(new Integer[] { 6, 5, 3 }));
        });

        // Missing with same values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(new Integer[] { 3, 3, 5, 6 }),
                Arrays.asList(new Integer[] { 6, 5, 3 }));
        });

        // Unexpected with distinct values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(new Double[] { 1.1, 2.2, 3.3 }),
                Arrays.asList(new Double[] { 3.3, 2.2, 2.1, 1.1 }));
        });

        // Unexpected with same values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(new Double[] { 1.1, 2.2, 3.3 }),
                Arrays.asList(new Double[] { 1.1, 2.2, 3.3, 3.3 }));
        });

        // Got something different but with the same count
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(new String[] { "X", "Y", "Z", "T" }),
                Arrays.asList(new String[] { "X", "Z", "Y", "U" }));
        });

        // Same count, same values, but different
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(new String[] { "X", "Y", "Z", "Z" }),
                Arrays.asList(new String[] { "X", "Z", "Y", "Y" }));
        });

        // Test empty stuff
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAllOnce(
                Collections.emptyList(),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Arrays.asList(new String[] { "Yolo" }),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAllOnce(
                Collections.emptyList(),
                Arrays.asList(new String[] { "Bruh" }));
        });
    }

    @Test
    void testAssertBrowseAll() {
        List<String> expected = Arrays.asList(new String[] { "a", "c", "b" });
        List<String> iterable = Arrays.asList(new String[] { "a", "b", "c" });

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
                Arrays.asList(new Integer[] { 3, 4, 5, 6 }),
                Arrays.asList(new Integer[] { 6, 5, 3 }));
        });

        // Missing with same values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(new Integer[] { 3, 3, 5, 6 }),
                Arrays.asList(new Integer[] { 6, 5, 3 }));
        });

        // Unexpected with distinct values
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(new Double[] { 1.1, 2.2, 3.3 }),
                Arrays.asList(new Double[] { 3.3, 2.2, 2.1, 1.1 }));
        });

        // Browsing twice the same value
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(new Double[] { 1.1, 2.2, 3.3 }),
                Arrays.asList(new Double[] { 1.1, 2.2, 3.3, 3.3 }));
        });

        // Got something different but with the same count
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(new String[] { "X", "Y", "Z", "T" }),
                Arrays.asList(new String[] { "X", "Z", "Y", "U" }));
        });

        // Expected twice the same item
        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(new String[] { "X", "Y", "Z", "Z" }),
                Arrays.asList(new String[] { "X", "Z", "Y", "Y" }));
        });

        // Test empty stuff
        assertDoesNotThrow(() -> {
            IteratorTester.assertBrowsesAll(
                Collections.emptyList(),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Arrays.asList(new String[] { "Yolo" }),
                Collections.emptyIterator());
        });

        assertThrows(AssertionFailedError.class, () -> {
            IteratorTester.assertBrowsesAll(
                Collections.emptyList(),
                Arrays.asList(new String[] { "Bruh" }));
        });
    }
}

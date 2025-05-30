package com.ignfab.minalac.generator.utils.iterator;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class IteratorTesterTest {

    @Test
    void testAssertBrowseAllOnce() {
        List<String> expected = List.of("a", "c", "b");
        List<String> iterable = List.of("a", "b", "c");

        // Should be the same in different orders, iterable or iterator
        assertDoesNotThrow(() -> assertBrowsesAllOnce(expected, iterable.iterator()));

        assertDoesNotThrow(() -> assertBrowsesAllOnce(expected, iterable));

        // Missing with distinct values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of(3, 4, 5, 6),
            List.of(6, 5, 3)
        ));

        // Missing with same values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of(3, 3, 5, 6),
            List.of(6, 5, 3)
        ));

        // Unexpected with distinct values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of(1.1, 2.2, 3.3),
            List.of(3.3, 2.2, 2.1, 1.1)
        ));

        // Unexpected with same values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of(1.1, 2.2, 3.3),
            List.of(1.1, 2.2, 3.3, 3.3)
        ));

        // Got something different but with the same count
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of("X", "Y", "Z", "T"),
            List.of("X", "Z", "Y", "U")
        ));

        // Same count, same values, but different
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of("X", "Y", "Z", "Z"),
            List.of("X", "Z", "Y", "Y")
        ));

        // Test empty stuff
        assertDoesNotThrow(() -> assertBrowsesAllOnce(
            Collections.emptyList(),
            Collections.emptyIterator()
        ));

        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            List.of("Yolo"),
            Collections.emptyIterator()
        ));

        assertThrows(AssertionFailedError.class, () -> assertBrowsesAllOnce(
            Collections.emptyList(),
            List.of("Bruh")
        ));
    }

    @Test
    void testAssertBrowseAll() {
        List<String> expected = List.of("a", "c", "b");
        List<String> iterable = List.of("a", "b", "c");

        // Should be the same in different orders, iterable or iterator
        assertDoesNotThrow(() -> assertBrowsesAll(expected, iterable.iterator()));

        assertDoesNotThrow(() -> assertBrowsesAll(expected, iterable));

        // Missing with distinct values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            List.of(3, 4, 5, 6),
            List.of(6, 5, 3)
        ));

        // Missing with same values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            List.of(3, 3, 5, 6),
            List.of(6, 5, 3)
        ));

        // Unexpected with distinct values
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            List.of(1.1, 2.2, 3.3),
            List.of(3.3, 2.2, 2.1, 1.1)
        ));

        // Browsing twice the same value
        assertDoesNotThrow(() -> assertBrowsesAll(
            List.of(1.1, 2.2, 3.3),
            List.of(1.1, 2.2, 3.3, 3.3)
        ));

        // Got something different but with the same count
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            List.of("X", "Y", "Z", "T"),
            List.of("X", "Z", "Y", "U")
        ));

        // Expected twice the same item
        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            List.of("X", "Y", "Z", "Z"),
            List.of("X", "Z", "Y", "Y")
        ));

        // Test empty stuff
        assertDoesNotThrow(() -> assertBrowsesAll(
            Collections.emptyList(),
            Collections.emptyIterator()
        ));

        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            List.of("Yolo"),
            Collections.emptyIterator()
        ));

        assertThrows(AssertionFailedError.class, () -> assertBrowsesAll(
            Collections.emptyList(),
            List.of("Bruh")
        ));
    }
}

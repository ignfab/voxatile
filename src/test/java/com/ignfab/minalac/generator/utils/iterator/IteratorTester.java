package com.ignfab.minalac.generator.utils.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test facility for iterators.
 */
public final class IteratorTester {

    // Make this utility class uninstantiable
    private IteratorTester() {}

    private static String prefix(String message) {
        return message == null ? "" : message + " ===> ";
    }

    /**
     * Tests {@code iterator} is empty.
     *
     * @param iterator iterator to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertEmpty(Iterator<T> iterator, String message) {
        if (iterator.hasNext())
            fail(prefix(message) + "<%s> expected to be empty".formatted(iterator));
    }

    /**
     * Tests {@code iterator} is empty.
     *
     * @param iterator iterator to test
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertEmpty(Iterator<T> iterator) {
        assertEmpty(iterator, null);
    }

    /**
     * Tests {@code iterable} is empty.
     *
     * @param iterable iterable to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterable
     */
    public static <T> void assertEmpty(Iterable<T> iterable, String message) {
        if (iterable.iterator().hasNext())
            fail(prefix(message) + "<%s> expected to be empty".formatted(iterable));
    }

    /**
     * Tests {@code iterable} is empty.
     *
     * @param iterable iterable to test
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertEmpty(Iterable<T> iterable) {
        assertEmpty(iterable, null);
    }

    /**
     * Tests {@code iterator} browses all values in {@code expected} and only them.
     * Uniqueness is not tested, same expected item can be browsed twice or more.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterator iterator to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertBrowsesAll(Collection<T> expected, Iterator<T> iterator, String message) {
        List<T> remaining = new ArrayList<>(expected);
        while (iterator.hasNext()) {
            T item = iterator.next();
            remaining.remove(item);
            if (!expected.contains(item))
                fail(prefix(message) + "unexpected item <%s> from <%s>".formatted(item, iterator));
        }
        if (!remaining.isEmpty())
            fail(prefix(message) + "%d items where not browsed by <%s>: %s".formatted(remaining.size(), iterator, remaining));
    }

    /**
     * Tests {@code iterator} browses all values in {@code expected} and only them.
     * Uniqueness is not tested, same expected item can be browsed twice or more.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterator iterator to test
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertBrowsesAll(Collection<T> expected, Iterator<T> iterator) {
        assertBrowsesAll(expected, iterator, null);
    }

    /**
     * Tests {@code iterable.iterator()} browses all values in {@code expected} and only them.
     * Uniqueness is not tested, same expected item can be browsed twice or more.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterable iterable to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterable
     */
    public static <T> void assertBrowsesAll(Collection<T> expected, Iterable<T> iterable, String message) {
        assertBrowsesAll(expected, iterable.iterator(), message);
    }

    /**
     * Tests {@code iterable.iterator()} browses all values in {@code expected} and only them.
     * Uniqueness is not tested, same expected item can be browsed twice or more.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterable iterable to test
     *
     * @param <T> Type of item for tested iterable
     */
    public static <T> void assertBrowsesAll(Collection<T> expected, Iterable<T> iterable) {
        assertBrowsesAll(expected, iterable.iterator(), null);
    }

    /**
     * Tests {@code iterator} browses all values in {@code expected}, only them and only once each.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterator iterator to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertBrowsesAllOnce(Collection<T> expected, Iterator<T> iterator, String message) {
        List<T> remaining = new ArrayList<>(expected);
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!remaining.remove(item))
                fail(prefix(message) + "unexpected item <%s> from <%s>".formatted(item, iterator));
        }
        if (!remaining.isEmpty())
            fail(prefix(message) + "%d items where not browsed by <%s>: %s".formatted(remaining.size(), iterator, remaining));
    }

    /**
     * Tests {@code iterator} browses all values in {@code expected}, only them and only once each.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterator iterator to test
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertBrowsesAllOnce(Collection<T> expected, Iterator<T> iterator) {
        assertBrowsesAllOnce(expected, iterator, null);
    }

    /**
     * Tests {@code iterable.iterator()} browses all values in {@code expected}, only them and only once each.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterable iterable to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterable
     */
    public static <T> void assertBrowsesAllOnce(Collection<T> expected, Iterable<T> iterable, String message) {
        assertBrowsesAllOnce(expected, iterable.iterator(), message);
    }

    /**
     * Tests {@code iterable.iterator()} browses all values in {@code expected}, only them and only once each.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterable iterable to test
     *
     * @param <T> Type of item for tested iterable
     */
    public static <T> void assertBrowsesAllOnce(Collection<T> expected, Iterable<T> iterable) {
        assertBrowsesAllOnce(expected, iterable.iterator(), null);
    }
}

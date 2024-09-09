package com.ignfab.minalac.generator.utils.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.opentest4j.AssertionFailedError;

/**
 * A test facility for iterators.
 */
public final class IteratorTester {

    // Make this utility class uninstanciable
    private IteratorTester() {}

    private static String prefix(String message) {
        if (message == null)
            return "";
        return message  + " ===> ";
    }

    /**
     * Test {@code iterator} browses all values in {@code expected} and only them.
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
        ArrayList<T> remaining = new ArrayList<T>(expected);
        while (iterator.hasNext()) {
            T item = iterator.next();
            remaining.remove(item);
            if (!expected.contains(item))
                throw new AssertionFailedError(prefix(message) + "unexpected item <%s> from <%s>".formatted(item.toString(), iterator.toString()));
        }
        if (remaining.size() > 0)
           throw new AssertionFailedError(prefix(message) + "%d items where not browsed by <%s>".formatted(remaining.size(), iterator.toString()));
    }

    /**
     * Test {@code iterator} browses all values in {@code expected} and only them.
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
     * Test {@code iterable.iterator()} browses all values in {@code expected} and only them.
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
     * Test {@code iterable.iterator()} browses all values in {@code expected} and only them.
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
     * Test {@code iterator} browses all values in {@code expected}, only them and only once each.
     * Order is not tested, only completeness.
     *
     * @param expected list of expected values
     * @param iterator iterator to test
     * @param message identifying message for the AssertionError
     *
     * @param <T> Type of item for tested iterator
     */
    public static <T> void assertBrowsesAllOnce(Collection<T> expected, Iterator<T> iterator, String message) {
        ArrayList<T> remaining = new ArrayList<T>(expected);
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!remaining.remove(item))
                throw new AssertionFailedError(prefix(message) + "unexpected item <%s> from <%s>".formatted(item.toString(), iterator.toString()));
        }
        if (remaining.size() > 0)
            throw new AssertionFailedError(prefix(message) + "%d items where not browsed by <%s>".formatted(remaining.size(), iterator.toString()));
    }

    /**
     * Test {@code iterator} browses all values in {@code expected}, only them and only once each.
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
     * Test {@code iterable.iterator()} browses all values in {@code expected}, only them and only once each.
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
     * Test {@code iterable.iterator()} browses all values in {@code expected}, only them and only once each.
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

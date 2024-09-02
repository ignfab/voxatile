package com.ignfab.minalac.generator.utils;

/**
 * A pair of two values.
 *
 * @param first first value
 * @param second second value
 * @param <T1> type of first value
 * @param <T2> type of second value
 */
public record Pair<T1, T2>(T1 first, T2 second) {}

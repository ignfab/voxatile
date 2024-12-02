package com.ignfab.minalac.generator.utils;

/**
 * An interval between two integers.
 *
 * @param begin start value of interval (included)
 * @param end end value of interval (included)
 */
public record IntegerInterval(int begin, int end) implements Comparable<IntegerInterval> {

    @Override
    public int compareTo(IntegerInterval other) {
        return other.begin() - begin;
    }

    /**
     * Tells if this interval overlaps another interval.
     *
     * @param other the other interval to test
     *
     * @return true if other interval overlaps.
     */
    public boolean overlaps(IntegerInterval other) {
        return (other.begin() <= end && other.end() >= begin);
    }
}


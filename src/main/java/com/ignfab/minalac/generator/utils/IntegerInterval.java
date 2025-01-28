package com.ignfab.minalac.generator.utils;

/**
 * An interval between two integers.
 *
 * @param begin start value of interval (included)
 * @param end end value of interval (included)
 */
public record IntegerInterval(int begin, int end) implements Comparable<IntegerInterval> {

    /**
     * Creates a new interval out of two values.
     *
     * @param begin Lower value of the interval (included)
     * @param end Higher value of the interval (included)
     */
    public IntegerInterval(int begin, int end) {
        if (begin > end)
            throw new IllegalArgumentException("Begin must be lesser than end.");
        this.begin = begin;
        this.end = end;
    }

    @Override
    public int compareTo(IntegerInterval other) {
        return (other.begin() != begin) ? (other.begin() - begin) : (other.end() - end);
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

    /**
     * Size in terms of how many integers are in interval (3 to 3 has size 1).
     *
     * @return size of the interval
     */
    public int size() {
        return end - begin + 1;
    }
}


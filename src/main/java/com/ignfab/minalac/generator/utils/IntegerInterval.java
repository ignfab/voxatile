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
    public IntegerInterval {
        if (begin > end)
            throw new IllegalArgumentException("Begin must be lesser than end.");
    }

    @Override
    public int compareTo(IntegerInterval other) {
        return (other.begin() != begin) ? (other.begin() - begin) : (other.end() - end);
    }

    /**
     * {@return the size of this interval}
     * It corresponds to how many integers are in interval (3 to 3 has size 1).
     */
    public int size() {
        return end - begin + 1;
    }

    /**
     * Checks if this interval contains the provided value.
     *
     * @param value the value to check.
     * @return true if the interval contains the value.
     */
    public boolean contains(int value) {
        return begin <= value && value <= end;
    }
}


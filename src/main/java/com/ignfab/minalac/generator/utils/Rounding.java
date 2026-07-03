package com.ignfab.minalac.generator.utils;

/**
 * Some advanced rounding helpers.
 * <p>
 * This class provides helpers for rounding not only to integers but to larger or smaller gaps.
 * For example, a gap of 2 rounds to even numbers.
 * A gap of 1 makes these function identical to those in Math class.
 */
public final class Rounding {
    private Rounding() {}

    /**
     * {@return closest number to {@code number} divisible by {@code gap}}
     *
     * @param number number to round
     * @param gap rounding gap
     */
    public static double round(double number, double gap) {
        return round(number, gap, 0);
    }

    /**
     * {@return closest number to {@code number} divisible by {@code gap} with {@code offset} gaps added}
     *
     * @param number number to round
     * @param gap rounding gap
     * @param offset number of gaps to add to result
     */
    public static double round(double number, double gap, int offset) {
        return (Math.round(number / gap) + offset) * gap;
    }

    /**
     * {@return largest number less than or equals to {@code number} and divisible by {@code gap}}
     *
     * @param number number to round
     * @param gap rounding gap
     */
    public static double floor(double number, double gap) {
        return floor(number, gap, 0);
    }

    /**
     * {@return largest number less than or equals to {@code number} and divisible by {@code gap} with {@code offset} gaps added}
     *
     * @param number number to round
     * @param gap rounding gap
     * @param offset number of gaps to add to result
     */
    public static double floor(double number, double gap, int offset) {
        return (Math.floor(number / gap) + offset) * gap;
    }

    /**
     * {@return smallest number greater than or equals to {@code number} and divisible by {@code gap}}
     *
     * @param number number to round
     * @param gap rounding gap
     */

    public static double ceil(double number, double gap) {
        return ceil(number, gap, 0);
    }

    /**
     * {@return smallest number greater than or equals to {@code number} and divisible by {@code gap} with {@code offset} gaps added}
     *
     * @param number number to round
     * @param gap rounding gap
     * @param offset number of gaps to add to result
     */

    public static double ceil(double number, double gap, int offset) {
        return (Math.ceil(number / gap) + offset) * gap;
    }
}

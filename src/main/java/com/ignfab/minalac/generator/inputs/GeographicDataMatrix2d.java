package com.ignfab.minalac.generator.inputs;

/**
 * A matrix of values.
 * This class is generic and should only be used through one of its concrete extension.
 * @see FloatGeographicDataMatrix2d
 *
 * @param <T> type of the value
 */
public interface GeographicDataMatrix2d<T> {

    /**
     * Returns a value at a given coordinates in the matrix.
     *
     * @param x x-coordinate from {@code 0} to {@code sizeX() - 1}
     * @param y y-coordinate from {@code 0} to {@code sizeY() - 1}
     *
     * @return value at given coordinates
     */
    T get(int x, int y);

    /**
     * {@return matrix size on x-axis (aka width)}
     */
    int sizeX();

    /**
     * {@return matrix size on y-axis (aka height)}
     */
    int sizeY();

    /**
     * {@return x-axis component of geographical offset}
     *
     * Beware, the offset should be relative to cells (pixels) centers, not corners.
     */
    double offsetX();

    /**
     * {@return y-axis component of geographical offset}
     *
     * Beware, the offset should be relative to cells (pixels) centers, not corners.
     */
    double offsetY();

    /**
     * {@return x-axis geographical size of matrix cells}
     */
    double cellSizeX();

    /**
     * {@return y-axis geographical size of matrix cells}
     */
    double cellSizeY();
}

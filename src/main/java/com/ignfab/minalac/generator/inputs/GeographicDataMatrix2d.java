package com.ignfab.minalac.generator.inputs;

/**
 * A matrix of values.
 * This class is generic and should only be used through one of its concrete extension.
 * @see FloatGeographicDataMatrix2d
 * @see IntegerGeographicDataMatrix2d
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
     * Returns matrix size on x-axis (aka width).
     *
     * @return matrix width
     */
    int sizeX();

    /**
     * Returns matrix size on y-axis (aka height).
     *
     * @return matrix height
     */
    int sizeY();

    /**
     * Return x-axis component of geographical offset.
     *
     * @return offset x-axis component
     */
    double offsetX();

    /**
     * Return y-axis component of geographical offset.
     *
     * @return offset y-axis component
     */
    double offsetY();

    /**
     * Return x-axis geographical size of matrix cells.
     *
     * @return size x-axis component
     */
    double cellSizeX();

    /**
     * Return y-axis geographical size of matrix cells.
     *
     * @return size y-axis component
     */
    double cellSizeY();
}

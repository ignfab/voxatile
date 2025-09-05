package com.ignfab.minalac.generator.inputs;

/**
 * A matrix of color values.
 */
public interface IntegerGeographicDataMatrix2d extends GeographicDataMatrix2d<Integer> {
    /**
     * @deprecated Use type-specific {@link #getColor(int, int)}
     */
    @Override
    @Deprecated
    default Integer get(int x, int y) {
        return getInt(x, y);
    }

    /**
     * Returns a int value at a given coordinates in the matrix.
     *
     * @param x x-coordinate from {@code 0} to {@code sizeX() - 1}
     * @param y y-coordinate from {@code 0} to {@code sizeY() - 1}
     *
     * @return color at given coordinates
     */
    int getInt(int x, int y);
}

package com.ignfab.minalac.generator.inputs;

/**
 * A matrix of float values.
 */
public interface FloatGeographicDataMatrix2d extends GeographicDataMatrix2d<Float> {
    /**
     * @deprecated Use type-specific {@link #getFloat(int, int)}
     */
    @Override
    @Deprecated
    default Float get(int x, int y) {
        return getFloat(x, y);
    }

    /**
     * Returns a float value at a given coordinates in the matrix.
     *
     * @param x x-coordinate from {@code 0} to {@code sizeX() - 1}
     * @param y y-coordinate from {@code 0} to {@code sizeY() - 1}
     *
     * @return float value at given coordinates
     */
    float getFloat(int x, int y);
}

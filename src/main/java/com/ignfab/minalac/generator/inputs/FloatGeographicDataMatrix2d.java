package com.ignfab.minalac.generator.inputs;

/**
 * A matrix of float values.
 * <p>
 * Beware: y-axis start from bottom and ends on top of the matrix.
 *
 * @param data float data as an array (must have a size of {@code sizeX} * {@code sizeY})
 * @param sizeX size of data matrix on x-axis
 * @param sizeY size of data matrix on y-axis
 * @param offsetX geographic offset for x-axis (position of matrix point 0 on map)
 * @param offsetY geographic offset for y-axis (position of matrix point 0 on map)
 * @param cellSizeX geographic size on x-axis of a matrix cell
 * @param cellSizeY geographic size on y-axis of a matrix cell
 */
public record FloatGeographicDataMatrix2d(
    float[] data,
    int sizeX,
    int sizeY,
    double offsetX,
    double offsetY,
    double cellSizeX,
    double cellSizeY
) implements GeographicDataMatrix2d<Float> {

    /**
     * Creates a new FloatGeographicDataMatrix2d with empty data.
     * @param sizeX size of data matrix on x-axis
     * @param sizeY size of data matrix on y-axis
     * @param offsetX geographic offset for x-axis (position of matrix point 0 on map)
     * @param offsetY geographic offset for y-axis (position of matrix point 0 on map)
     * @param cellSizeX geographic size on x-axis of a matrix cell
     * @param cellSizeY geographic size on y-axis of a matrix cell
     */
    public FloatGeographicDataMatrix2d(int sizeX, int sizeY, double offsetX, double offsetY, double cellSizeX, double cellSizeY) {
        this(new float[sizeX * sizeY], sizeX, sizeY, offsetX, offsetY, cellSizeX, cellSizeY);
    }

    @Override
    public Float get(int x, int y) {
        return data[x + (sizeY - y - 1) * sizeX];
    }
}

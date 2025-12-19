package com.ignfab.minalac.generator.inputs;

import org.eclipse.imagen.iterator.RandomIter;

/**
 * A matrix of float values.
 * <p>
 * Beware: y-axis start from bottom and ends on top of the matrix.
 *
 * @param data float data as a view of an image (must be of size {@code sizeX} * {@code sizeY})
 * @param dataOriginX pixel offset inside the data view on x-axis
 * @param dataOriginY pixel offset inside the data view on y-axis
 * @param sizeX size of data matrix on x-axis
 * @param sizeY size of data matrix on y-axis
 * @param offsetX geographic offset for x-axis (position of matrix point 0 on map)
 * @param offsetY geographic offset for y-axis (position of matrix point 0 on map)
 * @param cellSizeX geographic size on x-axis of a matrix cell
 * @param cellSizeY geographic size on y-axis of a matrix cell
 */
public record FloatImageGeographicDataMatrix2d(
    RandomIter data,
    int dataOriginX,
    int dataOriginY,
    int sizeX,
    int sizeY,
    double offsetX,
    double offsetY,
    double cellSizeX,
    double cellSizeY
) implements FloatGeographicDataMatrix2d {
    @Override
    public float getFloat(int x, int y) {
        return data.getSampleFloat(dataOriginX + x, dataOriginY + sizeY - y - 1, 0);
    }
}

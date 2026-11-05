package fr.ign.voxatile.core.inputs;

import java.awt.image.BufferedImage;

/**
 * A matrix of integer values.
 * <p>
 * Beware: y-axis start from bottom and ends on top of the matrix.
 *
 * @param data integer data as a view of an image (must be of size {@code sizeX} * {@code sizeY})
 * @param sizeX size of data matrix on x-axis
 * @param sizeY size of data matrix on y-axis
 * @param offsetX geographic offset for x-axis (position of matrix point 0 on map)
 * @param offsetY geographic offset for y-axis (position of matrix point 0 on map)
 * @param cellSizeX geographic size on x-axis of a matrix cell
 * @param cellSizeY geographic size on y-axis of a matrix cell
 */
public record IntegerImageGeographicDataMatrix2d(
    BufferedImage data,
    int sizeX,
    int sizeY,
    double offsetX,
    double offsetY,
    double cellSizeX,
    double cellSizeY
) implements IntegerGeographicDataMatrix2d {

    @Override
    public int getInteger(int x, int y) {
        return data.getRGB(x, y);
    }
}

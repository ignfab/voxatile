package fr.ign.voxatile.core.inputs;

import java.awt.image.BufferedImage;
import java.util.function.IntToDoubleFunction;

/**
 * Represents a matrix of floating-point geographic data backed by a BufferedImage.
 *
 * @param image the backing BufferedImage
 * @param decoder the float decoder
 * @param cellSizeX the size of a cell in the X direction
 * @param cellSizeY the size of a cell in the Y direction
 * @param offsetX the X offset of the matrix
 * @param offsetY the Y offset of the matrix
 */
public record FloatBufferedImageMatrix(
    BufferedImage image,
    IntToDoubleFunction decoder,
    double cellSizeX,
    double cellSizeY,
    double offsetX,
    double offsetY
) implements FloatGeographicDataMatrix2d {

    @Override
    public int sizeX() {
        return image.getWidth();
    }

    @Override
    public int sizeY() {
        return image.getHeight();
    }

    @Override
    public float getFloat(int x, int y) {
        int rgb = image.getRGB(x, image.getHeight() - 1 - y);
        return (float) decoder.applyAsDouble(rgb);
    }
}

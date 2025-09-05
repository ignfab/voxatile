package com.ignfab.minalac.generator.inputs;

import java.awt.image.BufferedImage;

// TODO: Parler de l'inversion y

public record BufferedImageGeographicDataMatrix2d(
    BufferedImage image,
    double offsetX,
    double offsetY,
    double cellSizeX,
    double cellSizeY
) implements IntegerGeographicDataMatrix2d {

    @Override
    public int getInt(int x, int y) {
        return image.getRGB(x, sizeY() - y - 1);
    }

    @Override
    public int sizeX() {
        return image.getWidth();
    }

    @Override
    public int sizeY() {
        return image.getHeight();
    }
}

package com.ignfab.minalac.generator.inputs;

import java.awt.image.BufferedImage;

public record ImageGeographicDataMatrix2d(
    BufferedImage image,
    double offsetX,
    double offsetY,
    double cellSizeX,
    double cellSizeY
) implements GeographicDataMatrix2d<Integer> {
    @Override
    public Integer get(int x, int y) {
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

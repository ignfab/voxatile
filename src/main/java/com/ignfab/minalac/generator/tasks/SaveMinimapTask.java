package com.ignfab.minalac.generator.tasks;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;

import com.ignfab.minalac.generator.generation.minimaps.Minimap;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * A {@code Task} used for saving a {@link Minimap} into specified file format.
 *
 * <p>
 * The {@code Minimap} is converted into a {@link BufferedImage}.
 */
public class SaveMinimapTask implements Task {
    private final Minimap minimap;
    private final String format;
    private final File destination;
    private final Color background;

    /**
     * Creates a new {@code SaveMinimapTask}.
     *
     * @param minimap minimap to save
     * @param destination minimap output destination relative to the world directory
     * @param format format to save the minimap in a format supported by {@link ImageIO#write}, e.g. "png", "jpg", "bmp", "gif"
     * @param background default background color for the minimap
     */
    public SaveMinimapTask(Minimap minimap, File destination, String format, Color background) {
        this.minimap = minimap;
        this.format = format.toLowerCase();
        this.destination = destination;
        this.background = background;
    }

    @Override
    public void run() {
        boolean supportsAlpha = ImageIO.getImageWriters(
            ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB),
            format
        ).hasNext();

        BufferedImage image = new BufferedImage(minimap.getWidth(), minimap.getHeight(), supportsAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < minimap.getWidth(); x++)
            for (int y = 0; y < minimap.getHeight(); y++) {
                if (minimap.get(x, y) == null && background != null) {
                    image.setRGB(x, minimap.getHeight() - 1 - y, background.getRGB());
                    continue;
                }
                image.setRGB(x, minimap.getHeight() - 1 - y, minimap.get(x, y).getRGB());
            }

        try {
            ImageIO.write(image, format, destination);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save minimap to " + destination, e);
        }
    }
}

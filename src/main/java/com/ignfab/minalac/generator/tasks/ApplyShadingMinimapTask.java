package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.minimaps.Minimap;
import com.ignfab.minalac.generator.generation.minimaps.MinimapCell;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Applies shading to a minimap based on its heightmap data.
 *
 * <p>
 * Shading simulates the effect of sunlight on terrain by adjusting pixel brightness
 * according to the slope and orientation of the terrain relative to a light source.
 * This post-processing effect enhances the visual perception of elevation changes.
 *
 * <p>
 * Note: The visual output is determined by the configured sun azimuth
 * and shadow intensity properties.
 */
public class ApplyShadingMinimapTask implements Task {
    private final Minimap minimap;
    private final double shadowIntensity;
    private final double sunDirectionX;
    private final double sunDirectionY;

    /**
     * Creates a new {@code ApplyShadingMinimapTask}.
     *
     * @param minimap the minimap to apply shading to
     * @param shadowIntensity the intensity of the shadows (0 to 1)
     * @param sunAzimuth the azimuth of the sun in radians
     */
    public ApplyShadingMinimapTask(Minimap minimap, double shadowIntensity, double sunAzimuth) {
        this.minimap = minimap;
        this.shadowIntensity = shadowIntensity;
        this.sunDirectionX = Math.cos(sunAzimuth);
        this.sunDirectionY = Math.sin(sunAzimuth);
    }

    /**
     * Computes the terrain slope projected onto the light direction.
     *
     * <p>
     * The local terrain gradient is estimated using central differences on the
     * heightmap. This gradient is then projected onto the light direction defined
     * by {@code sunDirectionX} and {@code sunDirectionY}. The returned value
     * represents how strongly the terrain faces the light source.
     *
     * <p>
     * Positive values indicate slopes facing the light, while negative values
     * indicate slopes facing away from it.
     *
     * @param x pixel x-coordinate
     * @param y pixel y-coordinate
     * @return the directional terrain slope. Returns {@code 1.0} for border
     *         pixels where the gradient cannot be computed.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Image_gradient">Image gradient</a>
     */
    private double computeDirectionalSlope(int x, int y) {
        if (x <= 0 || y <= 0 || x >= minimap.getWidth() - 1 || y >= minimap.getHeight() - 1)
            return 1.0;

        // TODO: Should be to parameterize the distance used for gradient computation (3 pixels and 2 pixels).
        double gx = minimap.get(x, y).getHeight() - minimap.get(x + 1, y).getHeight();
        double gy = minimap.get(x, y).getHeight() - minimap.get(x, y + 1).getHeight();
        return (gx * sunDirectionX) + (gy * sunDirectionY);
    }

    @Override
    public void run() {
        for (int x = 0; x < minimap.getWidth(); x++) {
            for (int y = 0; y < minimap.getHeight(); y++) {
                // Math.atan compresses extreme values and avoids harsh contrast
                double slopeFactor = 1.0 + Math.atan(computeDirectionalSlope(x, y) * shadowIntensity) / Math.PI;

                MinimapCell cell = minimap.get(x, y);
                cell.set(
                    cell.getRed() * slopeFactor,
                    cell.getGreen() * slopeFactor,
                    cell.getBlue() * slopeFactor,
                    cell.getAlpha(),
                    cell.getHeight()
                );
            }
        }
    }
}

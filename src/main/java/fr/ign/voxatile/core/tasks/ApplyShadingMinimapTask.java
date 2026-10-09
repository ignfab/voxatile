package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.generation.minimaps.Minimap;
import fr.ign.voxatile.core.generation.minimaps.MinimapCell;
import fr.ign.voxatile.core.utils.execution.Task;

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
        this.sunDirectionX = Math.cos(sunAzimuth) * shadowIntensity;
        this.sunDirectionY = Math.sin(sunAzimuth) * shadowIntensity;
    }

    /**
     * Computes the terrain slope projected onto the light direction.
     *
     * @param x minimap pixel x-coordinate
     * @param y minimap pixel y-coordinate
     * @return the computed directional slope at the specified minimap pixel.
     */
    private double computeDirectionalSlope(int x, int y) {
        MinimapCell neighborX = minimap.get(x + (sunDirectionX > 0 ? 1 : -1), y);
        MinimapCell neighborY = minimap.get(x, y + (sunDirectionY > 0 ? 1 : -1));

        double height = minimap.get(x, y).getHeight();
        return heightDiff(height, neighborX) * Math.abs(sunDirectionX)
            + heightDiff(height, neighborY) * Math.abs(sunDirectionY);
    }

    private double heightDiff(double height, MinimapCell neighbor) {
        return neighbor == null ? 0.0 : height - neighbor.getHeight();
    }

    @Override
    public void run() {
        for (int x = 0; x < minimap.getWidth(); x++) {
            for (int y = 0; y < minimap.getHeight(); y++) {
                MinimapCell cell = minimap.get(x, y);
                if (cell == null) continue;

                double slope = computeDirectionalSlope(x, y);
                double slopeFactor = 1.0 + slope / (1.0 + Math.abs(slope));

                // TODO: this way is dirty, as it directly modifies the cell's color based on the slope factor. Implements minimap layers
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

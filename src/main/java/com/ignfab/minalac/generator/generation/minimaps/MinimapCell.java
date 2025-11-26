package com.ignfab.minalac.generator.generation.minimaps;

import com.ignfab.minalac.generator.world.Voxel;

/**
 * Represents a single cell in the minimap 2D grid.
 *
 * <p>
 * A {@code MinimapCell} acts as a weighted accumulator for multiple {@link Voxel}
 * contributions projected onto the same grid position.
 * It stores aggregated color (RGBA) and height data before averaging.
 *
 * <p>
 * Each contribution is accumulated using a weight, allowing smooth blending between
 * multiple voxels mapped to the same cell.
 *
 * <p>
 * Final values are obtained as weighted averages computed using the sum of all
 * accumulated weights.
 *
 * <p>
 * This class does not represent a final pixel but an intermediate aggregation state
 * used during minimap data population.
 */
public class MinimapCell {

    // Accumulated weighted color components.
    private double red = 0d;
    private double green = 0d;
    private double blue = 0d;
    private double alpha = 0d;
    // Accumulated weighted height value.
    private double height = 0d;
    // Sum of all contribution weights, used to compute weighted averages.
    private double totalWeight = 0d;

    private double computeAverage(double accumulatedValue) {
        return totalWeight == 0 ? 0 : accumulatedValue / totalWeight;
    }

    /**
     * Adds a weighted contribution to this cell.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @param alpha the alpha color component
     * @param height the height value
     * @param weight the influence of this contribution
     */
    public void add(double red, double green, double blue, double alpha, double height, double weight) {
        this.red += red * weight;
        this.green += green * weight;
        this.blue += blue * weight;
        this.alpha += alpha * weight;
        this.height += height * weight;
        this.totalWeight += weight;
    }

    /**
     * Sets this cell to a single explicit value, replacing any previous contributions.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @param alpha the alpha color component
     * @param height the height value
     */
    public void set(double red, double green, double blue, double alpha, double height) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.height = height;
        totalWeight = 1;
    }

    /**
     * {@return the red component}
     */
    public double getRed() {
        return computeAverage(red);
    }

    /**
     * {@return the green component}
     */
    public double getGreen() {
        return computeAverage(green);
    }

    /**
     * {@return the blue component}
     */
    public double getBlue() {
        return computeAverage(blue);
    }

    /**
     * {@return the alpha component}
     */
    public double getAlpha() {
        return computeAverage(alpha);
    }

    /**
     * {@return the RGB value}
     */
    public int getRGB() {
        return Math.max(0, Math.min(255, (int) getAlpha())) << 24
            | (Math.max(0, Math.min(255, (int) getRed())) << 16)
            | (Math.max(0, Math.min(255, (int) getGreen())) << 8)
            | (Math.max(0, Math.min(255, (int) getBlue())) << 0);
    }

    /**
     * {@return the height value}
     */
    public double getHeight() {
        return computeAverage(height);
    }
}

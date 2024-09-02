package com.ignfab.minalac.generator.utils.world2d;

/**
 * A vector in 2 dimensions.
 *
 * @param x x-axis component of the vector
 * @param y y-axis component of the vector
 */
public record Vector2d(double x, double y) {
    /**
     * Computes the square of the vector length.
     *
     * @return square of the vector length.
     */
    public double squareLength() {
        return x * x + y * y;
    }

    /**
     * Computes the vector length.
     *
     * @return the vector length.
     */
    public double length() {
        return Math.sqrt(squareLength());
    }

    /**
     * Creates a new normalized (i.e. with a length of 1) vector with same direction.
     *
     * @return a normalized vector.
     */
    public Vector2d normalized() {
        double l = length();
        if (l > 0)
            return new Vector2d(x / l, y / l);
        else
            return new Vector2d(0.0, 0.0);
    }

    /**
     * Computes the component of any point, along that vector.
     *
     * @param x x-axis component of point position
     * @param y y-axis component of point position
     *
     * @return component of the point along the vector.
     */
    public double component(double x, double y) {
        return (this.x * x + this.y * y) / squareLength();
    }
}

package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;

/**
 * A vector in 3 dimensions.
 *
 * @param x x-axis component of the vector
 * @param y y-axis component of the vector
 * @param z z-axis component of the vector
 */
public record Vector3d(double x, double y, double z) {
    /**
     * Computes the square of the vector length.
     *
     * @return square of the vector length.
     */
    public double squareLength() {
        return x * x + y * y + z * z;
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
    public Vector3d normalized() {
        double l = length();
        if (l > 0)
            return new Vector3d(x / l, y / l, z / l);
        else
            return new Vector3d(0.0, 0.0, 0.0);
    }

    /**
     * Computes the component of any point, in the XY plane, along that vector.
     * <p>
     * componentXY() is different from toXY().component() as the length may be different.
     *
     * @param x x-axis component of point position
     * @param y y-axis component of point position
     *
     * @return component of the point along the vector.
     */
    public double componentXY(double x, double y) {
        return (this.x * x + this.y * y) / squareLength();
    }

    /**
     * Projects the vector on XY plane.
     *
     * @return Projection of vector on XY plane.
     */
    public Vector2d toXY() {
        return new Vector2d(x, y);
    }
}

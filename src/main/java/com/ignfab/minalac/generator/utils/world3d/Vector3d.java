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
     * A {@code Vector3d} instance for null vector.
     */
    public static final Vector3d ZERO = new Vector3d(0, 0, 0);

    /**
     * Compute length of a vector with given components.
     * <p>
     * Simplified shortcut for {@code new Vector(x, y, z).length()}.
     *
     * @param x x-axis component of the vector
     * @param y y-axis component of the vector
     * @param z z-axis component of the vector
     * @return vector length
     */
    public static double length(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * {@return vector length}
     */
    public double length() {
        return length(x, y, z);
    }

    /**
     * {@return true if vector is zero vector}
     */
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * Returns a new vector resulting from addition of a vector to this vector.
     *
     * @param vector vector to add
     * @return resulting vector
     */
    public Vector3d add(Vector3d vector) {
        return new Vector3d(x + vector.x, y + vector.y, z + vector.z);
    }

    /**
     * Returns a new vector resulting from subtraction of a vector from this vector.
     *
     * @param vector vector to subtract
     * @return resulting vector
     */
    public Vector3d subtract(Vector3d vector) {
        return new Vector3d(x - vector.x, y - vector.y, z - vector.z);
    }

    /**
     * Returns a new vector resulting of multiplication of this vector by a factor.
     *
     * @param factor factor to apply
     * @return resulting vector
     */
    public Vector3d multiply(double factor) {
        return new Vector3d(x * factor, y * factor, z * factor);
    }

    /**
     * {@return a new vector in the opposite direction of this vector, with same length}
     */
    public Vector3d opposite() {
        return new Vector3d(-x, -y, -z);
    }

    /**
     * {@return rounded vector coordinates as a new {@link WorldCoords3d}}
     */
    public WorldCoords3d round() {
        return WorldCoords3d.round(x, y, z);
    }

    /**
     * Flattens this vector to a {@link Vector2d} in x-y plane, getting rid of z-axis component.
     *
     * @return flattened 2d vector
     */
    public Vector2d to2d() {
        return new Vector2d(x, y);
    }

    @Override
    public String toString() {
        return "Vector3d(%f, %f, %f)".formatted(x, y, z);
    }

    // Overriding needed to use == instead of Double.equals that thinks 0.0 != -0.0
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3d that = (Vector3d) o;
        return x == that.x && y == that.y && z == that.z;
    }

    // Required because of equals overriding
    @Override
    public int hashCode() {
        return 31 * (31 * Double.hashCode(x) + Double.hashCode(y)) + Double.hashCode(z);
    }
}

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
    public static final Vector3d ZERO = new Vector3d(0, 0, 0); // Spécific class ?

    /**
     * Compute length of a vector with given components.
     *
     * Simplified shortcut for {@code new Vector(x, y, z).length()}.
     *
     * @param x x-axis component of the vector
     * @param y y-axis component of the vector
     * @param z z-axis component of the vector
     *
     * @return vector length
     */
    public static double length(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Computes length of this vector.
     *
     * @return vector length
     */
    public double length() {
        return length(x, y, z);
    }

    /**
     * Tells if vector is a zero vector.
     *
     * @return true if vector is zero vector.
     */
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * Returns normalized vector. If vector is zero, zero vector is returned.
     *
     * @return normalized vector
     */
    // TODO: TESTS
    public Vector3d normalized() {
        if (isZero())
            return ZERO;
        double length = length();
        return new Vector3d(x / length, y / length, z / length);
    }

    /**
     * Returns a new vector resulting from addition of a vector to this vector.
     *
     * @param vector vector to add
     *
     * @return resulting vector
     */
    public Vector3d add(Vector3d vector) {
        return new Vector3d(x + vector.x, y + vector.y, z + vector.z);
    }

    /**
     * Returns a new vector resulting from subtraction of a vector from this vector.
     *
     * @param vector vector to subtract
     *
     * @return resulting vector
     */
    public Vector3d subtract(Vector3d vector) {
        return new Vector3d(x - vector.x, y - vector.y, z - vector.z);
    }

    /**
     * Returns a new vector resulting of multiplication of this vector by a factor.
     *
     * @param factor factor to apply
     *
     * @return resulting vector
     */
    public Vector3d multiply(double factor) {
        return new Vector3d(x * factor, y * factor, z * factor);
    }

    /**
     * Returns a new vector in the oposite direction of this vector, with same lenght.
     *
     * @return opposite vector
     */
    public Vector3d opposite() {
        return new Vector3d(-x, -y, -z);
    }

    /**
     * Rounds that vector into a new {@link WorldCoords3d}.
     *
     * @return rounded coordinates
     */
    public WorldCoords3d round() {
        return WorldCoords3d.round(x, y, z);
    }

    /**
     * Computes scalar product of this vector with another vector as 3 coordinates.
     * @param x x-coordinate of vector to compute product with
     * @param y y-coordinate of vector to compute product with
     * @param z z-coordinate of vector to compute product with
     *
     * @return scalar product
     */
    // TODO: TESTS
    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    /**
     * Computes scalar product of this vector with another vector.
     * @param vector vector to compute product with
     *
     * @return scalar product
     */
    // TODO: TESTS
    public double dot(Vector3d vector) {
        return dot(vector.x, vector.y, vector.z);
    }

    /**
     * Computes product of this vector with another vector.
     * @param vector vector to compute product with
     *
     * @return vector product
     */
    // TODO: TESTS
    public Vector3d cross(Vector3d vector) {
        return new Vector3d(
            y * vector.z - z * vector.y,
            z * vector.x - x * vector.z,
            x * vector.y - y * vector.x
        );
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
        return "%s(%f, %f, %f)".formatted(this.getClass().getSimpleName(), x, y, z);
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

package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.Vector3d;

/**
 * A vector in 2 dimensions.
 *
 * @param x x-axis component of the vector
 * @param y y-axis component of the vector
 */
public record Vector2d(double x, double y) {

    /**
     * A {@code Vector2d} instance for null vector.
     */
    public static final Vector2d ZERO = new Vector2d(0, 0);

    /**
     * Computes length of a vector with given components.
     * <p>
     * Simplified shortcut for {@code new Vector(x, y).length()}.
     *
     * @param x x-axis component of the vector
     * @param y y-axis component of the vector
     *
     * @return vector length
     */
    public static double length(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * {@return vector length}
     */
    public double length() {
        return length(x, y);
    }

    /**
     * {@return true if vector is zero vector}
     */
    public boolean isZero() {
        return x == 0 && y == 0;
    }

    /**
     * Returns a new vector resulting from addition of a vector to this vector.
     *
     * @param vector vector to add
     * @return resulting vector
     */
    public Vector2d add(Vector2d vector) {
        return new Vector2d(x + vector.x, y + vector.y);
    }

    /**
     * Returns a new vector resulting from subtraction of a vector from this vector.
     *
     * @param vector vector to subtract
     * @return resulting vector
     */
    public Vector2d subtract(Vector2d vector) {
        return new Vector2d(x - vector.x, y - vector.y);
    }

    /**
     * Returns a new vector resulting of multiplication of this vector by a factor.
     *
     * @param factor factor to apply
     * @return resulting vector
     */
    public Vector2d multiply(double factor) {
        return new Vector2d(x * factor, y * factor);
    }

   /**
     * {@return a new vector in the opposite direction of this vector, with same length}
     */
    public Vector2d opposite() {
        return new Vector2d(-x, -y);
    }

    /**
     * {@return rounded vector coordinates as a new {@link WorldCoords2d}}.
     */
    public WorldCoords2d round() {
        return WorldCoords2d.round(x, y);
    }

    /**
     * Creates a {@link Vector3d} from this vector by adding given z-axis component.
     *
     * @param z z-axis component to add
     * @return resulting 3d vector
     */
    public Vector3d to3d(double z) {
        return new Vector3d(x, y, z);
    }

    /**
     * Returns normal of this vector as a new vector.
     * <p>
     * Normal is on starboard (right side when looking towards vector direction).
     *
     * @return the normal of this vector
     */
    public Vector2d normal() {
        return new Vector2d(y, -x);
    }

    /**
     * Computes the determinant of this vector with another vector as separate coordinates.
     *
     * @param x x-coordinate of vector to compute determinant with
     * @param y y-coordinate of vector to compute determinant with
     * @return determinant
     */
    public double determinant(double x, double y) {
        return this.x * y - this.y * x;
    }

    /**
     * Computes the determinant of this vector with another vector.
     *
     * @param vector vector to compute determinant with
     * @return determinant
     */
    public double determinant(Vector2d vector) {
        return determinant(vector.x, vector.y);
    }

    /**
     * Computes scalar product of this vector with another vector as separate coordinates.
     *
     * @param x x-coordinate of vector to compute scalar product with
     * @param y y-coordinate of vector to compute scalar product with
     * @return scalar product
     */
    public double dot(double x, double y) {
        return this.x * x + this.y * y;
    }

    /**
     * Computes scalar product of this vector with another vector.
     *
     * @param vector vector to compute scalar product with
     * @return scalar product
     */
    public double dot(Vector2d vector) {
        return dot(vector.x, vector.y);
    }

    @Override
    public String toString() {
        return "Vector2d(%f, %f)".formatted(x, y);
    }

    // Overriding needed to use == instead of Double.equals that thinks 0.0 != -0.0
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2d that = (Vector2d) o;
        return x == that.x && y == that.y;
    }

    // Required because of equals overriding
    @Override
    public int hashCode() {
        return 31 * Double.hashCode(x) + Double.hashCode(y);
    }

    public Vector2d normalize() {
        double norm = this.length();
        return new Vector2d(x / norm, y / norm);
    }
}

package com.ignfab.minalac.generator.voxelization.shape2d;

import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d line segment in the voxel world.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = direction.x() * index + start.x()
 *  y = direction.y() * index + start.y()
 * }</pre>
 * As {@code direction} is a unit vector, points of the segment correspond to {@code 0 <= index <= length}.
 */
public class Segment2d implements Bounded2d {
    private final WorldCoords2d start;
    private final WorldCoords2d end;
    private final WorldBBox2d bbox;
    private final Vector2d direction;
    private final double length;

    /**
     * Creates a new line segment between the given start and end points.
     *
     * @param start the start of the segment.
     * @param end the end of the segment.
     */
    public Segment2d(WorldCoords2d start, WorldCoords2d end) {
        this.start = start;
        this.end = end;
        bbox = new WorldBBox2d(start, end);
        if (start.equals(end)) {
            length = 0;
            direction = Vector2d.ZERO;
        } else {
            length = Vector2d.length(end.x() - start.x(), end.y() - start.y());
            direction = new Vector2d((end.x() - start.x()) / length, (end.y() - start.y()) / length);
        }
    }

    /**
     * {@return the starting point of the segment}
     */
    public WorldCoords2d start() {
        return start;
    }

    /**
     * {@return the ending point of the segment}
     */
    public WorldCoords2d end() {
        return end;
    }

    /**
     * Returns segment direction unit vector or {@code Vector2d.ZERO} if segment is a single point.
     *
     * @return Segment direction unit vector.
     */
    public Vector2d direction() {
        return direction;
    }

    /**
     * {@return length of line segment in voxels along line axis}
     */
    public double length() {
        return length;
    }

    /**
     * Returns segment normal unit vector or {@code Vector2d.ZERO} if segment is a single point.
     *
     * @return Segment normal unit vector.
     */
    public Vector2d normal() {
        return direction.normal();
    }

    /**
     * Computes the coordinate of the point on the segment's line at the given index.
     *
     * @param index the index in the segment's line (can be outside segment).
     * @return the voxel coordinate for this index.
     */
    public WorldCoords2d atIndex(double index) {
        return WorldCoords2d.round(
            direction.x() * index + start.x(),
            direction.y() * index + start.y()
        );
    }

    /**
     * Returns index of segment nearest point to a given point.
     * <p>
     * Nearest point is also the orthogonal projection of the given point on the segment.
     *
     * @param x x-axis coordinate of given point
     * @param y y-axis coordinate of given point
     * @return index in segment, may be negative or greater than segment length
     */
    public double nearestPointIndex(int x, int y) {
        return direction.dot(x - start.x(), y - start.y());
    }

    /**
     * Returns index of segment nearest point to a given positioned object.
     *
     * @param positioned positioned object
     * @return index in line, may be negative or greater than segment length
     */
    public double nearestPointIndex(Positioned2d positioned) {
        return nearestPointIndex(positioned.coords().x(), positioned.coords().y());
    }

    /**
     * Returns minimal distance from segment's line to a given point.
     * <p>
     * This distance could be negative depending on which side of the segment's line the given point is.
     * Use absolute value of returned distance if needed.
     *
     * @param x x-axis coordinate of given point
     * @param y y-axis coordinate of given point
     * @return distance to segment's line, may be negative depending on side of the line.
     */
    public double signedDistanceTo(double x, double y) {
        return direction.determinant(x - start.x(), y - start.y());
    }

    /**
     * Returns minimal distance from segment's line to a given positioned object.
     * <p>
     * This distance could be negative depending on which side of the segment's line the positioned object is.
     * Use absolute value of returned distance if needed.
     *
     * @param positioned positioned object
     * @return distance to segment's line, may be negative depending on side of the line.
     */
    public double signedDistanceTo(Positioned2d positioned) {
        return signedDistanceTo(positioned.coords().x(), positioned.coords().y());
    }

    /**
     * Returns minimal distance from segment's line to a given point expressed as a {@link Vector2d}.
     * <p>
     * This distance could be negative depending on which side of the segment's line the given point is.
     * Use absolute value of returned distance if needed.
     *
     * @param vector Vector representing point poisition
     * @return distance to segment's line, may be negative depending on side of the line.
     */
    public double signedDistanceTo(Vector2d vector) {
        return signedDistanceTo(vector.x(), vector.y());
    }

    public WorldCoords2d intersection(Segment2d segment) {
        double determinant = direction.determinant(segment.direction);
        if (determinant == 0)
            return null;
        // Finding the intersection point is solving a system of two line equations, which can be put in a matrix equation.
        // The calculation below is simply the result of matrix equation A * X = C  <=> X = A⁻¹ * C
        // A is matrix of line coefficients.
        // C is the vector of constants (right-hand side)
        // X is the intersection point
        return new WorldCoords2d(
            (int) Math.round((direction.x() * segment.direction.x() * (start.y() - segment.start.y()) + direction.x() * segment.direction.y() * segment.start.x() - direction.y() * segment.direction.x() * start.x()) / determinant),
            (int) Math.round((direction.y() * segment.direction.y() * (segment.start.x() - start.x()) + direction.x() * segment.direction.y() * start.y() - direction.y() * segment.direction.x() * segment.start.y()) / determinant)
        );
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return "%s[start=%s, end=%s]".formatted(getClass().getSimpleName(), start, end);
    }

    @Override
    public int hashCode() {
        return 31 * start.hashCode() + end.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment2d that = (Segment2d) o;
        return start.equals(that.start) && end.equals(that.end);
    }
}

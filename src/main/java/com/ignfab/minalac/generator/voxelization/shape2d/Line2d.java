package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d line segment in the voxel world.
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = direction.x() * index + start.x()
 *  y = direction.y() * index + start.y()
 * }</pre>
 * With {@code 0 <= index <= length}.
 *
 * Index is decimal. As {@code direction} is a unit vector, index is in voxels along the line axis.
 *
 * A minimal voxelization algorithm (thin line) could be obtained using indices that are multiple of {@code length / (max(bbox.sizeX(), bbox.sizeY()) - 1)}.
 */
public class Line2d implements Bounded2d, Shape2d {
    private final WorldCoords2d start;
    private final WorldCoords2d end;
    private final WorldBBox2d bbox;
    private final Vector2d direction;
    private final double length;

    /**
     * Creates a new line between the given start and end.
     * Also computes voxelization-related values.
     *
     * @param start the start of the line.
     * @param end the end of the line.
     */
    public Line2d(WorldCoords2d start, WorldCoords2d end) {
        this.start = start;
        this.end = end;
        bbox = new WorldBBox2d(start, end);
        if (start.equals(end)) {
            length = 0;
            direction = Vector2d.ZERO;
        } else {
            length = Vector2d.length(bbox.sizeX() - 1, bbox.sizeY() - 1);
            direction = new Vector2d((end.x() - start.x()) / length, (end.y() - start.y()) / length);
        }
    }

    /**
     * Returns the start of the line.
     *
     * @return the start of the line.
     */
    public WorldCoords2d start() {
        return start;
    }

    /**
     * Returns the end of the line.
     *
     * @return the end of the line.
     */
    public WorldCoords2d end() {
        return end;
    }

    /**
     * Returns line direction unit vector or {@code Vector2d.ZERO} if line is a single point.
     *
     * @return Line direction unit vector.
     */
    public Vector2d direction() {
        return direction;
    }

    /**
     * Return length of line segment in voxels along line axis.
     *
     * @return length
     */
    public double length() {
        return length;
    }

    /**
     * Computes the coordinate of the point on the line at the given index.
     * This corresponds to the coordinate of the nth voxel of this line.
     *
     * @param index the index in the line (could be outside segment).
     * @return the voxel coordinate for this index.
     */
    public WorldCoords2d atIndex(double index) {
        return WorldCoords2d.round(
            direction.x() * index + start.x(),
            direction.y() * index + start.y()
        );
    }

    /**
     * Returns index in line nearest to (x, y) point.
     *
     * @param x x-axis cordinate of given point
     * @param y y-axis cordinate of given point
     * @return index in line, may be negative or greater than segment length
     */
    public double indexAt(int x, int y) {
        return (x - start.x()) * direction.x() + (y - start.y()) * direction.y();
    }

    /**
     * Converts given coordinate into line relative coordinate.
     * <p>
     * Resulting vector x will be along the line, resulting vector y will be orthogonal to the line.
     *
     * @param x x-axis component of coordinates to convert
     * @param y y-axis component of coordinates to convert
     *
     * @return a {@link Vector2d} with relative to line corresponding coordinates.
     */
    public Vector2d convertLineRelative(int x, int y) {
        x = x - start().x();
        y = y - start().y();

        // This works because direction is a unit vector
        return new Vector2d(x * direction.x() + y * direction.y(), x * direction.y() - y * direction.x());
    }

    /**
     * Converts given coordinate into line relative coordinate.
     * <p>
     * Resulting vector x will be along the line, resulting vector y will be orthogonal to the line.
     *
     * @param coords coordinates to convert
     *
     * @return a {@link Vector2d} with relative to line corresponding coordinates.
     */
    public Vector2d convertLineRelative(WorldCoords2d coords) {
        return convertLineRelative(coords.x(), coords.y());
    }

    /**
     * Converts given vector coordinate into line relative coordinate.
     * <p>
     * Resulting vector x will be along the line, resulting vector y will be orthogonal to the line.
     *
     * @param v {@link Vector2d} representing given point coordinates
     *
     * @return a {@link Vector2d} with relative to line corresponding coordinates.
     */
    public Vector2d convertLineRelative(Vector2d v) {
        double x = v.x() - start().x();
        double y = v.y() - start().y();

        // This works because direction is a unit vector
        return new Vector2d(x * direction.x() + y * direction.y(), x * direction.y() - y * direction.x());
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return "%s{start=%s, end=%s}".formatted(getClass().getSimpleName(), start, end);
    }

    // Shape2d implementation

    @Override
    public Iterable<Line2d> lines() {
        return Collections.singleton(this);
    }

    // Line2d could be a LineString2d with only one segment. Maybe done if needed.
}

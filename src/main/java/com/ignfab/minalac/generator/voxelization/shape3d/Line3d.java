package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

/**
 * Represents a 3d line segment in the voxel world.
 * A few operations are done when creating a line
 * to compute values needed during voxelization.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = direction.x() * index + start.x()
 *  y = direction.y() * index + start.y()
 *  z = direction.z() * index + start.z()
 * }</pre>
 * With {@code 0 <= index <= length}.
 */
public class Line3d implements Bounded3d, Shape3d {
    private final WorldCoords3d start;
    private final WorldCoords3d end;
    private final WorldBBox3d bbox;
    private final Vector3d direction;
    private final double length;

    /**
     * Creates a new line between the given start and end.
     * Also computes voxelization-related values.
     *
     * @param start the start of the line.
     * @param end the end of the line.
     */
    public Line3d(WorldCoords3d start, WorldCoords3d end) {
        this.start = start;
        this.end = end;
        bbox = new WorldBBox3d(start, end);

        if (start.equals(end)) {
            length = 0;
            direction = Vector3d.ZERO;
        } else {
            length = Vector3d.length(bbox.sizeX() - 1, bbox.sizeY() - 1, bbox.sizeZ() - 1);
            direction = new Vector3d((end.x() - start.x()) / length, (end.y() - start.y()) / length,  (end.z() - start.z()) / length);
        }
    }

    /**
     * Returns the start of the line.
     *
     * @return the start of the line.
     */
    public WorldCoords3d start() {
        return start;
    }

    /**
     * Returns the end of the line.
     *
     * @return the end of the line.
     */
    public WorldCoords3d end() {
        return end;
    }

    /**
     * Returns line direction unit vector or {@code Vector3d.ZERO} if line is a single point.
     *
     * @return Line direction unit vector.
     */
    public Vector3d direction() {
        return direction;
    }

    /**
     * Returns the length of line segment in voxels along line axis.
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
    public WorldCoords3d atIndex(double index) {
        return WorldCoords3d.round(
            direction.x() * index + start.x(),
            direction.y() * index + start.y(),
            direction.z() * index + start.z()
        );
    }

    /**
     * Returns index in line nearest to (x, y) point, ignoring z third dimension.
     * <p>
     * It is equivalent to, but faster than, {@code .to2d().indexAt(x, y)}.
     *
     * @param x x-axis cordinate of given point
     * @param y y-axis cordinate of given point
     * @return index in line, may be negative or greater than segment length
     */
    public double indexAt(int x, int y) {
        return (x - start.x()) * direction.x() + (y - start.y()) * direction.y();
    }

    /**
     * Projects this line on X-Y plane as a new {@link Line2d}.
     *
     * @return projected 2d line
     */
    public Line2d to2d() {
        return new Line2d(start.to2d(), end.to2d());
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return "%s{start=%s, end=%s}".formatted(getClass().getSimpleName(), start, end);
    }

    // Shape3d implementation

    @Override
    public Iterable<Line3d> lines() {
        return Collections.singleton(this);
    }
}


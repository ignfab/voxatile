package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.shape2d.iterator.Line2dIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldMilliCoords2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;

/**
 * Represents a 2d line segment in the voxel world.
 * It stores start and end in milli-voxel precision.
 * A few operations are done when creating a line
 * to compute values needed during voxelization.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = ax * t + bx
 *  y = ay * t + by
 * }</pre>
 * With {@code 0 <= t <= tMax}.
 *
 * @see #maxIndex() tMax
 * @see #directionX() ax
 * @see #directionY() ay
 * @see #originX() bx
 * @see #originY() by
 */
public class Line2d implements Iterable<IndexedVoxel2d> {
    private final WorldMilliCoords2d start;
    private final WorldMilliCoords2d end;

    // 0 <= t <= tMax
    // { x = ax * t + bx
    // { y = ay * t + by
    private final int tMax;
    private final double ax;
    private final double bx;
    private final double ay;
    private final double by;

    /**
     * Creates a new line between the given start and end.
     * Also computes voxelization-related values.
     *
     * @param start the start of the line.
     * @param end the end of the line.
     */
    public Line2d(WorldMilliCoords2d start, WorldMilliCoords2d end) {
        this.start = start;
        this.end = end;

        // At t == 0, we are at start position
        bx = start.realX();
        by = start.realY();
        // Compute direction vector of the line
        double dx = end.realX() - bx;
        double dy = end.realY() - by;

        // Find the direction of the line (x or y)
        // to compute tMax and scale direction vector
        // to get a step of 1 voxel in the line direction
        double x = Math.abs(dx);
        double y = Math.abs(dy);
        double dir;
        if (x < 1e-4 && y < 1e-4) {
            // If start == end, there is no direction so
            // we take a value to prevent division by 0
            dir = 1e-4;
            tMax = 0;
        } else if (x >= y) {
            dir = x;
            tMax = Math.abs(end.x() - start.x());
        } else {
            dir = y;
            tMax = Math.abs(end.y() - start.y());
        }
        // Scale direction vector to get voxel step
        ax = dx / dir;
        ay = dy / dir;
    }

    /**
     * Returns the start of the line.
     *
     * @return the start of the line.
     */
    public WorldMilliCoords2d start() {
        return start;
    }

    /**
     * Returns the end of the line.
     *
     * @return the end of the line.
     */
    public WorldMilliCoords2d end() {
        return end;
    }

    /**
     * Returns the maximum value the index can be when voxelizing the line.
     * This corresponds to the length in number of voxel of this line.
     *
     * @return the maximum index value.
     */
    public int maxIndex() {
        return tMax;
    }

    /**
     * Returns the x-component of a direction vector of the line.
     *
     * @return the x-component of a direction vector of the line.
     */
    public double directionX() {
        return ax;
    }

    /**
     * Returns the y-component of a direction vector of the line.
     *
     * @return the y-component of a direction vector of the line.
     */
    public double directionY() {
        return ay;
    }

    /**
     * Returns the x-component of the origin point of the line.
     *
     * @return the x-component of the origin point of the line.
     */
    public double originX() {
        return bx;
    }

    /**
     * Returns the y-component of the origin point of the line.
     *
     * @return the y-component of the origin point of the line.
     */
    public double originY() {
        return by;
    }

    /**
     * Computes the coordinate of the point on the line at the given index.
     * This corresponds to the coordinate of the nth voxel of this line.
     *
     * @param index the index in the line.
     * @return the coordinate in milli-voxel precision.
     */
    public WorldMilliCoords2d atIndex(double index) {
        return WorldMilliCoords2d.fromWorldCoords(ax * index + bx, ay * index + by);
    }

    /**
     * Returns an iterator over the voxels of this line.
     * Each voxel will be indexed by its position in the line,
     * with an index between {@code 0} and {@link #maxIndex()}.
     *
     * @return a new {@link Line2dIterator} on this line.
     */
    @Override
    public Line2dIterator iterator() {
        return new Line2dIterator(this);
    }

    @Override
    public String toString() {
        return "Line2d{start=%s, end=%s}".formatted(start, end);
    }
}

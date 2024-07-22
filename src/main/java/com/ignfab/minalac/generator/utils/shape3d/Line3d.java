package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.shape3d.iterator.Line3dIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldMilliCoords3d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel3d;

/**
 * Represents a 3d line segment in the voxel world.
 * It stores start and end in milli-voxel precision.
 * A few operations are done when creating a line
 * to compute values needed during voxelization.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = ax * t + bx
 *  y = ay * t + by
 *  z = az * t + bz
 * }</pre>
 * With {@code 0 <= t <= tMax}.
 *
 * @see #maxIndex() tMax
 * @see #directionX() ax
 * @see #directionY() ay
 * @see #directionZ() az
 * @see #originX() bx
 * @see #originY() by
 * @see #originZ() bz
 */
public class Line3d implements Iterable<IndexedVoxel3d> {
    private final WorldMilliCoords3d start;
    private final WorldMilliCoords3d end;

    // 0 <= t <= tMax
    // { x = ax * t + bx
    // { y = ay * t + by
    // { z = az * t + bz
    private final int tMax;
    private final double ax;
    private final double bx;
    private final double ay;
    private final double by;
    private final double az;
    private final double bz;

    /**
     * Creates a new line between the given start and end.
     * Also computes voxelization-related values.
     *
     * @param start the start of the line.
     * @param end the end of the line.
     */
    public Line3d(WorldMilliCoords3d start, WorldMilliCoords3d end) {
        this.start = start;
        this.end = end;

        // At t == 0, we are at start position
        bx = start.realX();
        by = start.realY();
        bz = start.realZ();
        // Compute direction vector of the line
        double dx = end.realX() - bx;
        double dy = end.realY() - by;
        double dz = end.realZ() - bz;

        // Find the direction of the line (x or y or z)
        // to compute tMax and scale direction vector
        // to get a step of 1 voxel in the line direction
        double x = Math.abs(dx);
        double y = Math.abs(dy);
        double z = Math.abs(dz);
        double dir;
        if (x < 1e-4 && y < 1e-4 && z < 1e-4) {
            // If start == end, there is no direction so
            // we take a value to prevent division by 0
            dir = 1e-4;
            tMax = 0;
        } else if (x >= y && x >= z) {
            dir = x;
            tMax = Math.abs(end.x() - start.x());
        } else if (y >= x && y >= z) {
            dir = y;
            tMax = Math.abs(end.y() - start.y());
        } else {
            dir = z;
            tMax = Math.abs(end.z() - start.z());
        }
        // Scale direction vector to get voxel step
        ax = dx / dir;
        ay = dy / dir;
        az = dz / dir;
    }

    /**
     * Returns the start of the line.
     *
     * @return the start of the line.
     */
    public WorldMilliCoords3d start() {
        return start;
    }

    /**
     * Returns the end of the line.
     *
     * @return the end of the line.
     */
    public WorldMilliCoords3d end() {
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
     * Returns the z-component of a direction vector of the line.
     *
     * @return the z-component of a direction vector of the line.
     */
    public double directionZ() {
        return az;
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
     * Returns the z-component of the origin point of the line.
     *
     * @return the z-component of the origin point of the line.
     */
    public double originZ() {
        return bz;
    }

    /**
     * Computes the coordinate of the point on the line at the given index.
     * This corresponds to the coordinate of the nth voxel of this line.
     *
     * @param index the index in the line.
     * @return the coordinate in milli-voxel precision.
     */
    public WorldMilliCoords3d atIndex(double index) {
        return WorldMilliCoords3d.fromWorldCoords(ax * index + bx, ay * index + by, az * index + bz);
    }

    /**
     * Returns an iterator over the voxels of this line.
     * Each voxel will be indexed by its position in the line,
     * with an index between {@code 0} and {@link #maxIndex()}.
     *
     * @return a new {@link Line3dIterator} on this line.
     */
    @Override
    public Line3dIterator iterator() {
        return new Line3dIterator(this);
    }

    @Override
    public String toString() {
        return "Line3d{start=%s, end=%s}".formatted(start, end);
    }
}

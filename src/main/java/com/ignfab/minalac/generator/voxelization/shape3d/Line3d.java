package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.Line3dIterator;

/**
 * Represents a 3d line segment in the voxel world.
 * A few operations are done when creating a line
 * to compute values needed during voxelization.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = slopeX * index + start.x
 *  y = slopeY * index + start.y
 *  z = slopeZ * index + start.z
 * }</pre>
 * With {@code 0 <= index <= maxIndex}.
 *
 * @see #maxIndex() maxIndex
 */
public class Line3d implements Bounded3d, Shape3d {
    private final WorldCoords3d start;
    private final WorldCoords3d end;

    // 0 <= index <= maxIndex
    // { x = slope.x() * index + start.x()
    // { y = slope.y() * index + start.y()
    // { z = slope.z() * index + start.z()
    private final int maxIndex;
    private Vector3d slope;

    private final WorldBBox3d bbox;

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

        // This will be involved in intersection calculation
        bbox = new WorldBBox3d(start, end);

        // Compute direction vector of the line
        int deltaX = end.x() - start.x();
        int deltaY = end.y() - start.y();
        int deltaZ = end.z() - start.z();

        // Maximum index is the largest coordinate distance
        maxIndex = Math.max(Math.abs(deltaX), Math.max(Math.abs(deltaY), Math.abs(deltaZ)));

        // Normalize vector to index
        if (maxIndex == 0) {
            slope = new Vector3d(0.0, 0.0, 0.0);
        } else {
            slope = new Vector3d(
                deltaX / (double) maxIndex,
                deltaY / (double) maxIndex,
                deltaZ / (double) maxIndex
            );
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
     * Returns the maximum value the index can be when voxelizing the line.
     * This corresponds to the length in number of voxel of this line.
     *
     * @return the maximum index value.
     */
    public int maxIndex() {
        return maxIndex;
    }

    /**
     * Returns slope vector.
     *
     * @return the slope vector.
     */
    public Vector3d slope() {
        return slope;
    }

    /**
     * Computes the coordinate of the point on the line at the given index.
     * This corresponds to the coordinate of the nth voxel of this line.
     *
     * @param index the index in the line.
     * @return the voxel coordinate for this index.
     */
    public WorldCoords3d atIndex(int index) {
        return WorldCoords3d.round(
            slope.x() * index + start.x(),
            slope.y() * index + start.y(),
            slope.z() * index + start.z()
        );
    }

    /**
     * Give Z value for a given index on the line (index may be out of line).
     *
     * @param index index which give Z value for
     * @return Z value at given index.
     */
    public int zAtIndex(int index) {
        return (int) Math.round(slope.z() * index + start.z());
    }

    /**
     * Computes index of orthogonal projection on the line from a given point (index may be out of line).
     *
     * @param x x-axis component of the point position
     * @param y y-axis component of the point position
     *
     * @return index, in the line, of the projected point.
     */
    public int projectedIndexFromXY(int x, int y) {
        return (int) Math.round(((x - start.x()) * slope.x() + (y - start.y()) * slope.y()) / (slope.x() * slope.x() + slope.y() * slope.y()));
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return "Line3d{start=%s, end=%s}".formatted(start, end);
    }

    @Override
    public Iterable<LinearVoxel3d> borderVoxels() {
        return () -> new Line3dIterator(this, null);
    }
}

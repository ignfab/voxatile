package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.Line3dConnectedIterator;
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
    // { x = slopeX * index + start.x
    // { y = slopeY * index + start.y
    // { z = slopeZ * index + start.z
    private final int maxIndex;
    private final double slopeX;
    private final double slopeY;
    private final double slopeZ;

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
            slopeX = 0.0;
            slopeY = 0.0;
            slopeZ = 0.0;
        } else {
            slopeX = deltaX / (double) maxIndex;
            slopeY = deltaY / (double) maxIndex;
            slopeZ = deltaZ / (double) maxIndex;
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
     * Computes the coordinate of the point on the line at the given index.
     * This corresponds to the coordinate of the nth voxel of this line.
     *
     * @param index the index in the line.
     * @return the voxel coordinate for this index.
     */
    public WorldCoords3d atIndex(int index) {
        return WorldCoords3d.round(
            slopeX * index + start.x(),
            slopeY * index + start.y(),
            slopeZ * index + start.z()
        );
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
    public Iterable<LineVoxel3d> borderVoxels() {
        return () -> new Line3dIterator(this);
    }


    @Override
    public Iterable<LineVoxel3d>connectedBorderVoxels() {
        return () -> new Line3dConnectedIterator(this);
    }

    public double slopeX() {
        return slopeX;
    }
    public double slopeY() {
        return slopeY;
    }
    public double slopeZ() {
        return slopeZ;
    }

}

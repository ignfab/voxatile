package com.ignfab.minalac.generator.voxelization.shape2d;

import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Line2dIterator;

/**
 * Represents a 2d line segment in the voxel world.
 * A few operations are done when creating a line
 * to compute values needed during voxelization.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = slopeX * index + start.x
 *  y = slopeY * index + start.y
 * }</pre>
 * With {@code 0 <= index <= maxIndex}.
 *
 * @see #maxIndex() maxIndex
 */
public class Line2d implements Bounded2d, Shape2d {
    private final WorldCoords2d start;
    private final WorldCoords2d end;

    // 0 <= index <= maxIndex
    // { x = slopeX * index + start.x
    // { y = slopeY * index + start.y
    private final int maxIndex;
    private Vector2d slope;

    private final WorldBBox2d bbox;

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

        // This will be involved in intersection calculation
        bbox = new WorldBBox2d(start, end);

        // Compute direction vector of the line
        int deltaX = end.x() - start.x();
        int deltaY = end.y() - start.y();

        // Maximum index is the largest coordinate distance
        maxIndex = Math.max(Math.abs(deltaX), Math.abs(deltaY));

        // Normalize vector to index
        if (maxIndex == 0)
            slope = new Vector2d(0.0, 0.0);
        else
            slope = new Vector2d(
                deltaX / (double) maxIndex,
                deltaY / (double) maxIndex
            );
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
    public WorldCoords2d atIndex(int index) {
        return WorldCoords2d.round(
            slope.x() * index + start.x(),
            slope.y() * index + start.y()
        );
    }

    /**
     * Computes all intersecting positions at a given Y-coordinate.
     * If line is rather horizontal than vertical, intersection concerns several voxels.
     * This is used for polygon filling. It could be used for line drawing if we would
     * not need an index position for each voxel.
     *
     * @param y Y-coordinate for which we want intersection positions
     *
     * @return An Intersection object or {@code null} if there is no intersection
     */
    public Intersection intersection(int y) {
        // No intersection
        if (y < bbox.minY() || y > bbox.maxY())
            return null;

        // Find start and end indexes of intersection (reciprocal computation from Y)
        // Ceils and floors depends on the line direction
        double startT;
        double endT;
        if (start.y() == end.y()) {
            // Horizontal line
            startT = 0;
            endT = maxIndex;
        } else if (slope.y() < 0) {
            // Ascending line
            startT = Math.max(0, Math.floor((y + 0.5 - start.y()) / slope.y()) + 1);
            endT = Math.min(maxIndex, Math.floor((y - 0.5 - start.y()) / slope.y()));
        } else {
            // Descending line
            startT = Math.max(0, Math.ceil((y - 0.5 - start.y()) / slope.y()));
            endT = Math.min(maxIndex, Math.ceil((y + 0.5 - start.y()) / slope.y()) - 1);
        }

        int x1 = (int) Math.round(slope.x() * endT + start.x());
        int x2 = (int) Math.round(slope.x() * startT + start.x());

        return new Intersection(
            Math.min(x1, x2),
            Math.max(x1, x2),
            y > bbox.minY(), // Line crosses top voxel line border if Y is not minimum Y
            y < bbox.maxY()  // Line crosses bottom voxel line border if Y is not maximum Y
        );
    }

    /**
     * An intersection with a line at given Y-coordinate.
     * Intersection is not only a point, it can be several voxels large if
     * line is rather vertical than horizontal. Here we store min and max
     * X-coordinate value of intersection, plus some information useful for
     * polygon filling (how does line crosses Y-coordinate voxel line).
     *
     * @param start Minimum X-coordinate of intersection
     * @param end Maximum X-coordinate of intersection
     * @param top True if the line crosses Y-coordinate voxel line upper side
     * @param bottom True if the line crosses Y-coordinate voxel line lower side
     */
    public record Intersection(int start, int end, boolean top, boolean bottom) implements Comparable<Intersection> {
        @Override
        public int compareTo(Intersection other) {
            return start - other.start;
        }
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return "Line2d{start=%s, end=%s}".formatted(start, end);
    }

    @Override
    public Iterable<LinearVoxel2d> borderVoxels() {
        return () -> new Line2dIterator(this);
    }

    /**
     * Returns slope vector.
     *
     * @return the slope vector.
     */
    public Vector2d slope() {
        return slope;
    }
}

package com.ignfab.minalac.generator.utils.shape2d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.shape2d.iterator.Line2dIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

/**
 * Represents a 2d line segment in the voxel world.
 * A few operations are done when creating a line
 * to compute values needed during voxelization.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = ax * t + start.x
 *  y = ay * t + start.y
 * }</pre>
 * With {@code 0 <= t <= tMax}.
 *
 * @see #maxIndex() tMax
 */
public class Line2d implements Shape2d {
    private final WorldCoords2d start;
    private final WorldCoords2d end;

    // 0 <= t <= tMax
    // { x = ax * t + start.x
    // { y = ay * t + start.y
    private final int tMax;
    private final double ax;
    private final double ay;

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
        int dx = end.x() - start.x();
        int dy = end.y() - start.y();

        // Maximum index is the largest coordinate distance
        tMax = Math.max(Math.abs(dx), Math.abs(dy));

        // Normalize vector to index
        if (tMax == 0) {
            ax = 0.0;
            ay = 0.0;
        } else {
            ax = dx / (double) tMax;
            ay = dy / (double) tMax;
        }
    }

    /**
     * Returns line bounding box.
     *
     * @return line bounding box.
     */
    public WorldBBox2d bbox() {
        return bbox;
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
        return tMax;
    }

    /**
     * Computes the coordinate of the point on the line at the given index.
     * This corresponds to the coordinate of the nth voxel of this line.
     *
     * @param index the index in the line.
     * @return the voxel coordinate for this index.
     */
    public WorldCoords2d atIndex(int index) {
        return new WorldCoords2d(
            (int) Math.round(ax * index + start.x()),
            (int) Math.round(ay * index + start.y())
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
        if (y < bbox.getMinY() || y > bbox.getMaxY())
            return null;

        // Find start and end indexes of intersection (reciprocal computation from Y)
        // Ceils and floors depends on the line direction
        double startT;
        double endT;
        if (start.y() == end.y()) {
            // Horizontal line
            startT = 0;
            endT = tMax;
        } else if (ay < 0) {
            // Ascending line
            startT = Math.max(0, Math.floor((y + 0.5 - start.y()) / ay) + 1);
            endT = Math.min(tMax, Math.floor((y - 0.5 - start.y()) / ay));
        } else {
            // Descending line
            startT = Math.max(0, Math.ceil((y - 0.5 - start.y()) / ay));
            endT = Math.min(tMax, Math.ceil((y + 0.5 - start.y()) / ay) - 1);
        }

        int x1 = (int) Math.round(ax * endT + start.x());
        int x2 = (int) Math.round(ax * startT + start.x());

        return new Intersection(
            Math.min(x1, x2),
            Math.max(x1, x2),
            y > bbox.getMinY(), // Line crosses top voxel line border if Y is not minimum Y
            y < bbox.getMaxY()  // Line crosses bottom voxel line border if Y is not maximum Y
        );
    }

    /**
     * An intersection with a line at given Y-coordinate.
     * Intersection is not only a point, it can be several voxels large if
     * line is rather vertical than horizontal. Here we store min and max
     * X-coordinate value of intersection, plus some information useful for
     * polygon filling (how does line crosses Y-coord voxel line).
     *
     * @param start Minimum X-coordinate of intersection
     * @param end Maximum X-coordinate of intersection
     * @param top True if the line crosses Y-coord voxel line upper side
     * @param bottom True if the line crosses Y-coord voxel line lower side
     */
    public record Intersection(int start, int end, boolean top, boolean bottom) implements Comparable<Intersection> {
        @Override
        public int compareTo(Intersection other) {
            return start - other.start;
        }
    }

    @Override
    public String toString() {
        return "Line2d{start=%s, end=%s}".formatted(start, end);
    }

    @Override
    public Iterable<LineVoxel2d> borderVoxels() {
        return () -> new Line2dIterator(this);
    }

    @Override
    public Iterable<Voxel2d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }
}

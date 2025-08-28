package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

/**
 * An iterator returning voxels of each position inside a 2d polygon.
 *
 * @see Polygon2d
 */
public class Polygon2dIterator implements Iterator<Positioned2d> {

    private final Polygon2d polygon;
    private final boolean includeBorders;

    private int x;
    private int y;
    private int endX;

    private Iterator<Intersection> iterator;
    private Intersection reuseIntersection;
    private int borderStart;
    private int borderEnd;
    private boolean borderChangingInsideness;
    private boolean reusePreviousBorder;

    /**
     * Creates a new iterator on the given polygon.
     *
     * @param polygon the polygon to iterate over.
     * @param includeBorders include borders in iteration
     *
     * <p>
     * This iterator supposes polygon borders are rendered with "thin line algorithm"
     * (minimal voxel iterator).
     *
     * <p>
     * Polygon iterator will browse each Y in polygon bbox.
     * It will fetch all line intersection with voxel line at that Y.
     * Each intersection is modeled by a start, an end (of x value), and two
     * flags indicating if this line crosses the top and/or bottom limit of
     * the voxel line.
     *
     * <p>
     * nextBorderRange merges touching and overlapping line intersections into
     * a voxel range and stores its limits in borderStart, borderEnd.
     * borderChangingInsideness indicates if, after crossing this border, we
     * have switched between inside and outside polygon.
     *
     * <p>
     * nextIterationRange determines range of iteration for X, depending on
     * whether borders are included or not.
     *
     * <p>
     * If they are, process is quite obvious: we start iterating at the
     * beginning of the first border found. If it does not change insideness,
     * we stop at its end. Otherwise, we stop at the end of the next border
     * changing insideness.
     *
     * <p>
     * If borders are excluded, we start iterating after the end of the
     * first border changing insideness (ignoring others) and stop before the
     * start of next border (which voxels should be excluded). If that border
     * does not change insideness, we have to get back iterating just after it
     * as if it were an "entering" border.
     *
     */

    // TODO (other PR): This iterator has to be revamped to simplify and clarify algorithm
    // TODO (other PR): includeBorders was there to have a voxelized polygon not overlapping eventual
    // border iterator. Now such iterator doesn't exist anymore, this flag should be replaced by something
    // like "thickness" adapted to polygons.
    public Polygon2dIterator(Polygon2d polygon, boolean includeBorders) {
        this.polygon = polygon;
        this.includeBorders = includeBorders;
        y = polygon.bbox().minY() - 1;
        iterator = Collections.emptyIterator();
        moveOn();
    }

    /**
     * Computes all intersecting positions of a line at a given Y-coordinate.
     * If line is rather horizontal than vertical, intersection concerns several voxels.
     * This is used for polygon filling. It could be used for line drawing if we would
     * not need an index position for each voxel.
     *
     * @param line Line to compute intersections with horizontal axis
     * @param y Y-coordinate for which we want intersection positions
     *
     * @return An Intersection object or {@code null} if there is no intersection
     */
    public static Intersection intersection(Segment2d line, int y) {
        // No intersection
        if (y < line.bbox().minY() || y > line.bbox().maxY())
            return null;

        int maxIndex = Math.max(line.bbox().sizeX(), line.bbox().sizeY()) - 1;
        double indexFactor;
        if (maxIndex > 0)
            indexFactor = line.length() / maxIndex;
        else
            indexFactor = 0;

        // Find start and end indexes of intersection (reciprocal computation from Y)
        // Ceils and floors depends on the line direction
        double startT;
        double endT;
        WorldCoords2d start = line.start();
        Vector2d direction = line.direction();

        if (start.y() == line.end().y()) {
            // Horizontal line
            startT = 0;
            endT = maxIndex;
        } else if (direction.y() < 0) {
            // Ascending line
            startT = Math.max(0, Math.floor((y + 0.5 - start.y()) / (direction.y() * indexFactor)) + 1);
            endT = Math.min(maxIndex, Math.floor((y - 0.5 - start.y()) / (direction.y() * indexFactor)));
        } else {
            // Descending line
            startT = Math.max(0, Math.ceil((y - 0.5 - start.y()) / (direction.y() * indexFactor)));
            endT = Math.min(maxIndex, Math.ceil((y + 0.5 - start.y()) / (direction.y() * indexFactor)) - 1);
        }

        int x1 = (int) Math.round(direction.x() * indexFactor * endT + start.x());
        int x2 = (int) Math.round(direction.x() * indexFactor * startT + start.x());

        return new Intersection(
            Math.min(x1, x2),
            Math.max(x1, x2),
            y > line.bbox().minY(), // Line crosses top voxel line border if Y is not minimum Y
            y < line.bbox().maxY()  // Line crosses bottom voxel line border if Y is not maximum Y
        );
    }

    /**
     * Lists intersections of this polygon at a given y.
     * Very useful for voxelization purpose.
     * <p>
     * {@code Line2d} computes intersection of a line at that given y.
     * This method lists all these intersection.
     *
     * @param y the y-component value of the straight line to intersect.
     * @return the list of intersections.
     */
    public List<Intersection> intersections(int y) {
        if (y < polygon.bbox().minY() || y > polygon.bbox().maxY())
            return Collections.emptyList();

        List<Intersection> intersections = new ArrayList<>();
        for (Segment2d line : polygon.segments()) {
            Intersection intersection = intersection(line, y);
            if (intersection != null)
                intersections.add(intersection);
        }

        return intersections;
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

    // Move X to the next border range (range of border voxels)
    // Returns false if no more iteration range found
    private boolean nextBorderRange() {
        // Reuse previous intersection or fetch a new one
        Intersection intersection = reuseIntersection;
        reuseIntersection = null;

        if (intersection == null) {
            if (!iterator.hasNext())
                return false;
            intersection = iterator.next();
        }

        borderStart = intersection.start();
        borderEnd = intersection.end();
        boolean top = intersection.top();
        boolean bottom = intersection.bottom();

        // Merge all overlapping and touching intersections
        while (iterator.hasNext()) {
            intersection = iterator.next();

            // An intersection is overlapping if it starts before or on the last voxel of merged intersection
            // An intersection is touching if it starts just after the last voxel of merged intersection
            if (intersection.start() > borderEnd + 1) {
                // Intersections are sorted by start(), we can stop on first non-touching, it will be included in next border
                reuseIntersection = intersection;
                break;
            }

            // Adjust top and bottom crossing flags (two crosses on the same side cancel each other)
            top ^= intersection.top();
            bottom ^= intersection.bottom();

            // Merged intersection end is extended to embed overlapping/touching intersection
            borderEnd = Math.max(borderEnd, intersection.end());
        }

        // Every border entering the merged intersection must also get out
        if (top ^ bottom)
            throw new IllegalStateException("Unclosed polygon");

        borderChangingInsideness = top && bottom;

        return true;
    }

    // Find next continuous iteration range for X, including borders
    private boolean nextIterationRangeIncludingBorders() {
        // Reach the next intersection (we will iterate on its voxels)
        if (!nextBorderRange())
            return false;

        // Start iteration at first voxel of the intersection
        x = borderStart;

        // Now, find intersection for ending iterations on X

        // If intersection had led us into polygon...
        if (borderChangingInsideness)
            // Jump to the next intersection changing insideness (leading outside polygon)
            do {
                if (!nextBorderRange())
                    throw new IllegalStateException("Unclosed polygon");
            } while (!borderChangingInsideness);

        // End iteration at last voxel of the intersection
        endX = borderEnd;

        return true;
    }

    // Find next continuous iteration range for X, excluding borders
    private boolean nextIterationRangeExcludingBorders() {
        // We may just have jump over an intersection that did not change insideness.
        // In that case, we have to resume iteration at its end (case of a non changing
        // intersection encountered in polygon)
        if (!reusePreviousBorder)
            // Go to the next intersection changing insideness (leading inside polygon)
            do {
                if (!nextBorderRange())
                    return false;
            } while (!borderChangingInsideness);

        // Starting iterations after intersection ends (only polygon inside)
        x = borderEnd + 1;

        // Now, find intersection for ending iterations on X:
        // We stop at next intersection (we do not want to iterate on its voxels)
        if (!nextBorderRange())
            throw new IllegalStateException("Unclosed polygon");

        // Ending iterations before intersection starts (only polygon inside)
        endX = borderStart - 1;

        // Reuse same border next time if it does not change insideness
        // (we will stay inside polygon after having jumped over it)
        reusePreviousBorder = !borderChangingInsideness;

        return true;
    }

    // Move X to the next iteration range (range of voxels included in results)
    // Returns false if no more iteration range found
    private boolean nextIterationRange() {
        return includeBorders ? nextIterationRangeIncludingBorders() : nextIterationRangeExcludingBorders();
    }

    // Go to next line
    private void nextLine() {
        // If borders are excluded, there may be lines with no iterations (only borders), so we have to loop
        do {
            y++;
            if (y > polygon.bbox().maxY())
                return; // Stops when out of polygon

            // Fetch all line intersections, sort them and creates an iterator
            List<Intersection> list = intersections(y);
            list.sort(null);
            iterator = list.iterator();

            // nextIterationRange will try to find an X iteration range for given intersection iterator
        } while (!nextIterationRange());
    }

    private void moveOn() {
        x++;
        if (x > endX && !nextIterationRange())
            nextLine();
    }

    @Override
    public boolean hasNext() {
        // We will have intersections until the very last scanline
        return y <= polygon.bbox().maxY();
    }

    @Override
    public Positioned2d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Positioned2d element = new WorldCoords2d(x, y);
        moveOn();
        return element;
    }
}

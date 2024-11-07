package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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

    private Iterator<Line2d.Intersection> iterator;
    private Line2d.Intersection reuseIntersection;
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
     */
    public Polygon2dIterator(Polygon2d polygon, boolean includeBorders) {
        this.polygon = polygon;
        this.includeBorders = includeBorders;
        y = polygon.bbox().minY() - 1;
        iterator = Collections.emptyIterator();
        moveOn();
    }

    // Move X to the next border range (range of border voxels)
    // Returns false if no more iteration range found
    private boolean nextBorderRange() {
        // Reuse previous intersection or fetch a new one
        Line2d.Intersection intersection = reuseIntersection;
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
            List<Line2d.Intersection> list = polygon.intersections(y);
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

        WorldCoords2d element = new WorldCoords2d(x, y);
        moveOn();
        return new Positioned2dWithSDF(element, polygon);
    }

    public record Positioned2dWithSDF(WorldCoords2d coords, Polygon2d polygon) implements Positioned2d {
        public double sdf() {
            double d = Double.MAX_VALUE;
            for (Line2d line : polygon.lines()) {
                int ex = line.start().x() - line.end().x();
                int ey = line.start().y() - line.end().y();
                int wx = coords.x() - line.end().x();
                int wy = coords.y() - line.end().y();
                double tmp = Math.min(Math.max(0, (wx * ex + wy * ey) / (double) (ex * ex + ey * ey)), 1);
                double bx = wx - ex * tmp;
                double by = wy - ey * tmp;
                d = Math.min(d, bx * bx + by * by);
            }
            return Math.sqrt(d);

        /*float sdPolygon( in vec2[N] v, in vec2 p )
        {
            float d = dot(p-v[0],p-v[0]);
            float s = 1.0;
            for( int i=0, j=N-1; i<N; j=i, i++ )
            {
                vec2 e = v[j] - v[i];
                vec2 w =    p - v[i];
                vec2 b = w - e*clamp( dot(w,e)/dot(e,e), 0.0, 1.0 );
                d = min( d, dot(b,b) );
                bvec3 c = bvec3(p.y>=v[i].y,p.y<v[j].y,e.x*w.y>e.y*w.x);
                if( all(c) || all(not(c)) ) s*=-1.0;
            }
            return s*sqrt(d);
        }*/
        }
    }
}

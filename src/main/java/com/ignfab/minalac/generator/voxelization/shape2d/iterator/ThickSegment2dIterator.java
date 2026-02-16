package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

/**
 * An iterator returning voxels along a {@link Segment2d} with a given thickness.
 * <p>
 * Segment with thickness is drawn as a trapezoid with:
 * <ul>
 * <li>two opposing sides parallel to the segment, named "borders"
 * <li>two other sides, not necessarily parallel, named "bevels"
 * </ul>
 * Bevels direction should be adjusted in order to draw nice connections with neighbor lines.
 */
 public class ThickSegment2dIterator implements Iterator<Positioned2d> {

    private final Segment2d segment;
    private final Vector2d startBevelDirection; // Bevel direction at starting point
    private final Vector2d endBevelDirection;   // Bevel direction at ending point

    // Computed values
    private final WorldBBox2d bbox;
    private final double thicknessY;            // Thickness projected on y-axis

    // Current state
    private int startY; // First y of current scanline
    private int endY;   // Last y of current scanline
    private int x;      // Current x (aka scanline x)
    private int y;      // Current y in scanline

    // Computes intersection between two infinite lines defined by a point (pX) and a direction (dX)
    private static Vector2d intersection(Vector2d p1, Vector2d d1, Vector2d p2, Vector2d d2) {
        double determinant = d1.determinant(d2);
        if (determinant == 0)
            return null;
        // Finding the intersection point is solving a system of two line equations, which can be put in a matrix equation.
        // The calculation below is simply the result of matrix equation A * X = C  <=> X = A⁻¹ * C
        // A is matrix of line coefficients.
        // C is the vector of constants (right-hand side)
        // X is the intersection point
        return new Vector2d(
            (d1.x() * d2.x() * (p1.y() - p2.y()) + d1.x() * d2.y() * p2.x() - d1.y() * d2.x() * p1.x()) / determinant,
            (d1.y() * d2.y() * (p2.x() - p1.x()) + d1.x() * d2.y() * p1.y() - d1.y() * d2.x() * p2.y()) / determinant
        );
    }

    /**
     * Creates a new iterator on a segment with predecessor and successor (and so, beveled ends).
     * <p>
     * These four parameters describes the four side of the surface to draw: two borders parallel to the segment and two bevels intersecting them.
     * <p>
     * Bevels direction must not be same as segment.
     * <p>
     * Start and end bevel direction must be in opposite direction.
     *
     * @param segment the segment to iterator over.
     * @param thickness thickness of the segment in voxels.
     * @param startBevelDirection unit vector for start bevel direction
     * @param endBevelDirection unit vector for end bevel direction
     */
    public ThickSegment2dIterator(Segment2d segment, double thickness, Vector2d startBevelDirection, Vector2d endBevelDirection) {
        this.segment = segment;
        this.startBevelDirection = startBevelDirection;
        this.endBevelDirection = endBevelDirection;

        // Case of single point segment: area cannot be computed
        if (segment.direction().isZero()) {
            bbox = WorldBBox2d.EMPTY;

            // Final (useless here) fields
            thicknessY = 0.0;

            // Force hasNext to return false
            x = bbox.maxX() + 1;
            y = 1;
            endY = 0;

            return;
        }

        // Intermediate values
        Vector2d halfThickVector = segment.normal().multiply(thickness * 0.5);

        // Computes what to add and subtract to Y component of the segment's line to get both inferior and superior thick line borders
        if (segment.bbox().sizeY() == 0)
            // Unused, vertical segment has a specific processing
            thicknessY = 0.0;
        else
            thicknessY = halfThickVector.dot(halfThickVector) / Math.abs(halfThickVector.y());

        Vector2d direction = segment.direction();
        Vector2d start = segment.start().toVector();
        Vector2d end = segment.end().toVector();

        // When end and start bevels intersects within thickness (thick segment then forms a triangle rather than a trapezoid)
        // bbox should be shrunk accordingly

        Vector2d bevelIntersection = intersection(start, startBevelDirection, end, endBevelDirection);
        double bevelIntersectionPosition = bevelIntersection == null ? Double.POSITIVE_INFINITY : segment.signedDistanceTo(bevelIntersection);

        Vector2d thickPoint1 = start.add(halfThickVector);
        Vector2d thickPoint2 = start.subtract(halfThickVector);

        // Test if bevels intersection is in segment thickness
        if (Math.abs(bevelIntersectionPosition) <= thickness) {
            Vector2d thickPoint = bevelIntersectionPosition < 0 ? thickPoint1 : thickPoint2;
            bbox = new WorldBBox2d(
                bevelIntersection.round(),
                intersection(start, startBevelDirection, thickPoint, direction).round(),
                intersection(end, endBevelDirection, thickPoint, direction).round()
            );
        } else
            bbox = new WorldBBox2d(
                intersection(start, startBevelDirection, thickPoint1, direction).round(),
                intersection(start, startBevelDirection, thickPoint2, direction).round(),
                intersection(end, endBevelDirection, thickPoint1, direction).round(),
                intersection(end, endBevelDirection, thickPoint2, direction).round()
            );

        // Position ourself on first scanline (scanlines are along x axis)
        x = bbox.minX() - 1;
        nextScanline();
    }

    /**
     * Returns an indexed version of this {@code ThickLine2dIterator}.
     * <p>
     * With each voxel position are returned:
     * <ul>
     *   <li>distance to nearest point in segment (could be negative depending on which side);
     *   <li>index of nearest point in segment (from 0 to segment length);
     * </ul>
     * Warning: The returned iterator is not a copy, and shares the same iteration cursor!
     *
     * @return indexed version of this {@code ThickLine2dIterator}
     */
    public Iterator<IndexedPosition2d> indexed() {
        return Iterators.remap(this, position -> new IndexedPosition2d(
            position,
            segment.nearestPointIndex(position),
            segment.signedDistanceTo(position))
        );
    }

    // Compute y-coordinate of a point in a given line (point, direction) at a given x-coordinate
    private int lineYforX(double x, WorldCoords2d point, Vector2d direction) {
        return (int) Math.round((x - point.x()) / direction.x() * direction.y() + point.y());
    }

    // Go to next scanline (if any)
    private void nextScanline() {
        // Scanlines are along x axis
        x++;
        if (x > bbox.maxX())
            return;

        Vector2d direction = segment.direction();
        WorldCoords2d start = segment.start();
        WorldCoords2d end = segment.end();

        // Compute startY and endY for the new scanline at x. Voxels will be between these two Y.

        // First focus on borders, we know all voxels are between them.
        if (direction.x() == 0) {
            // Vertical segment, we rely on bbox for borders
            startY = bbox.minY();
            endY = bbox.maxY();
        } else {
            // This is y in segment for current x
            double y = lineYforX(x, start, direction);
            startY = (int) Math.round(y - thicknessY);
            endY = (int) Math.round(y + thicknessY);
        }

        // Now, remove what is over start bevel
        if (startBevelDirection.x() != 0) {
            int y = lineYforX(x, start, startBevelDirection);
            if (startBevelDirection.x() > 0)
                startY = Math.max(startY, y);
            else
                endY = Math.min(endY, y);
        }

        // Then, remove what is over end bevel
        if (endBevelDirection.x() != 0) {
            int y = lineYforX(x, end, endBevelDirection);
            if (endBevelDirection.x() > 0)
                startY = Math.max(startY, y);
            else
                endY = Math.min(endY, y);
        }

        // Place y at the beginning of the new scanline
        y = startY;
    }

    @Override
    public boolean hasNext() {
        return x <= bbox.maxX() || y <= endY;
    }

    @Override
    public Positioned2d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        WorldCoords2d coords = new WorldCoords2d(x, y);

        y++;
        if (y > endY)
            nextScanline();

        return coords;
    }
}

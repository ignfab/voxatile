package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

/**
 * An iterator over voxels of a {@link Line2d} with thickness.
 */
 public class ThickLine2dIterator implements Iterator<Positioned2d> {

    private final Line2d line;
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
        return new Vector2d(
            (d1.x() * d2.x() * (p1.y() - p2.y()) + d1.x() * d2.y() * p2.x() - d1.y() * d2.x() * p1.x()) / determinant,
            (d1.y() * d2.y() * (p2.x() - p1.x()) + d1.x() * d2.y() * p1.y() - d1.y() * d2.x() * p2.y()) / determinant
        );
    }

    /**
     * Creates a new iterator on a line with predecessor and successor (and so, beveled ends).
     *
     * @param line the line to iterator over.
     * @param thickness thickness of the line in voxels.
     * @param startBevelDirection unit vector for start bevel direction
     * @param endBevelDirection unit vector for end bevel direction
     *
     * Start and end bevel direction must be in oposite direction.
     */
    public ThickLine2dIterator(Line2d line, double thickness, Vector2d startBevelDirection, Vector2d endBevelDirection) {
        this.line = line;
        this.startBevelDirection = startBevelDirection;
        this.endBevelDirection = endBevelDirection;

        // Case of single point line : area cannot be computed
        if (line.direction().isZero()) {
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
        Vector2d normal = line.direction().normal();
        Vector2d thickVector = normal.multiply(thickness * 0.5);

        // Computes y component to add and subtract to line y to get its full width
        thicknessY = (thickVector.y() * thickVector.y() + thickVector.x() * thickVector.x()) / Math.abs(thickVector.y());

        // When end and start borders intersects within thickness
        // (line then forms a triangle rather than a trapezoid)
        // bbox should be shrunk accordingly
        Vector2d start = line.start().toVector();
        Vector2d end = line.end().toVector();
        Vector2d thickPoint1 = start.add(thickVector);
        Vector2d thickPoint2 = start.subtract(thickVector);

        // Test if bevels intersection is in line thickness
        Vector2d bevelIntersection = intersection(start, startBevelDirection, end, endBevelDirection);
        double bevelIntersectionPosition = Double.POSITIVE_INFINITY;
        if (bevelIntersection != null)
            bevelIntersectionPosition = line.convertLineRelative(bevelIntersection).y();

        if (Math.abs(bevelIntersectionPosition) <= thickness)
            if (bevelIntersectionPosition > 0)
                bbox = new WorldBBox2d(
                    bevelIntersection.round(),
                    intersection(start, startBevelDirection, thickPoint2, line.direction()).round(),
                    intersection(end, endBevelDirection, thickPoint2, line.direction()).round()
                );
            else
                bbox = new WorldBBox2d(
                    bevelIntersection.round(),
                    intersection(start, startBevelDirection, thickPoint1, line.direction()).round(),
                    intersection(end, endBevelDirection, thickPoint1, line.direction()).round()
                );
        else
            bbox = new WorldBBox2d(
                intersection(start, startBevelDirection, thickPoint1, line.direction()).round(),
                intersection(start, startBevelDirection, thickPoint2, line.direction()).round(),
                intersection(end, endBevelDirection, thickPoint1, line.direction()).round(),
                intersection(end, endBevelDirection, thickPoint2, line.direction()).round()
            );
        x = bbox.minX() - 1;
        nextScanline();
        y = startY;
    }


    /**
     * Creates a new iterator on a standalone line.
     * <p>
     * Line may have successor and predecessor. In that case its ends are beveled according to relative angle.
     *
     * @param line the line to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLine2dIterator(Line2d line, double thickness) {
        this(line, thickness, line.direction().normal(), line.direction().normal().opposite());
    }

    // Go to next scanline (if any)
    private void nextScanline() {
        x++;
        if (x > bbox.maxX())
            return;

        // Compute startY and endY for the new scanline at x

        // Right and left borders
        if (line.direction().x() == 0) {
            // Vertical line, we rely on bbox for borders
            startY = bbox.minY();
            endY = bbox.maxY();
        } else {
            // This is y in line for current x
            double y = (x - line.start().x()) * line.direction().y() / line.direction().x() + line.start().y();
            startY = (int) Math.round(y - thicknessY);
            endY = (int) Math.round(y + thicknessY);
        }

        // Start bevel
        if (startBevelDirection.x() != 0) {
            double y = (x - line.start().x()) * startBevelDirection.y() / startBevelDirection.x() + line.start().y();
            if (startBevelDirection.x() > 0)
                startY = Math.max(startY, (int) Math.round(y));
            else
                endY = Math.min(endY, (int) Math.round(y));
        }

        // End bevel
        if (endBevelDirection.x() != 0) {
            double y = (x - line.end().x()) * endBevelDirection.y() / endBevelDirection.x() + line.end().y();
            if (endBevelDirection.x() > 0)
                startY = Math.max(startY, (int) Math.round(y));
            else
                endY = Math.min(endY, (int) Math.round(y));
        }

        // Place y at the begining of the new scanline
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

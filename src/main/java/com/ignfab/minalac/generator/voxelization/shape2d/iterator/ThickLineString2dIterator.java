package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

/**
 * An iterator returning voxels along a {@link LineString2d} with a given thickness.
 */
 public class ThickLineString2dIterator implements Iterator<Positioned2d> {
    private final LineString2d lineString;
    private final double thickness;

    private int index = -1;
    private Iterator<Positioned2d> segmentIterator = null;

    /**
     * Creates a new line string iterator with thickness.
     *
     * @param lineString the line string to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineString2dIterator(LineString2d lineString, double thickness) {
        this.lineString = lineString;
        this.thickness = thickness;
    }

    private Vector2d computeBevelDirection(Segment2d segment, Vector2d normal) {
        if (segment == null)
            return normal;

        Vector2d direction = normal.add(segment.normal());

        // In case of other segment in perfect opposite direction:
        if (direction.isZero())
            return normal;
        return direction;
    }

    protected void prepare() {
        while ((segmentIterator == null || !segmentIterator.hasNext()) && index < lineString.size() - 1) {
            index++;
            Segment2d segment = getCurrentSegment();

            // Direction should never be zero but it costs nothing to check
            if (!segment.direction().isZero()) {
                Segment2d next = lineString.get(index + 1);
                Segment2d previous = lineString.get(index - 1);
                Vector2d normal = segment.normal();

                segmentIterator = new ThickSegment2dIterator(
                    segment,
                    thickness,
                    computeBevelDirection(previous, normal),
                    computeBevelDirection(next, normal).opposite());
            }
        }
    }

    /**
     * {@return the {@link Segment2d} currently iterating on}
     */
    public Segment2d getCurrentSegment() {
        return lineString.get(index);
    }

    @Override
    public boolean hasNext() {
        prepare();
        return segmentIterator != null && segmentIterator.hasNext();
    }

    @Override
    public Positioned2d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return segmentIterator.next();
    }
}

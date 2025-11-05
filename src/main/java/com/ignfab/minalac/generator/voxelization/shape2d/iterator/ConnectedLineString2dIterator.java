package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;

/**
 * An iterator over voxels of a {@link Line2d} with thickness.
 */
 public class ConnectedLineString2dIterator implements Iterator<Positioned2d> {
    private final LineString2d lineString;
    private final Double delta;

    private int index = 0;
    private Iterator<Positioned2d> iterator = null;

    /**
     * Creates a new lineString iterator whith thickness.
     *
     * @param lineString the lineString to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ConnectedLineString2dIterator(LineString2d lineString, double delta) {
        this.lineString = lineString;
        this.delta = delta;
    }

    private void prepare() {

        while ((iterator == null || !iterator.hasNext()) && index < lineString.size()) {
            Segment2d segment = lineString.get(index);
            Vector2d normal = segment.normal();

            // Normal should never be zero but it costs nothing to check
            if (!normal.isZero()) {
                Segment2d next = lineString.get(index + 1);
                Segment2d previous = lineString.get(index - 1);

                Vector2d startShift;

                if (previous == null) {
                    startShift = normal.multiply(delta);
                } else {
                    startShift = normal.add(previous.normal()).unit();
                    startShift = startShift.multiply(delta / Math.abs(segment.direction().determinant(startShift)));
                }

                Vector2d endShift;

                if (next == null) {
                    endShift = normal.multiply(delta);
                } else {
                    endShift = normal.add(next.normal()).unit();
                    endShift = endShift.multiply(delta / Math.abs(segment.direction().determinant(endShift)));
                }

                Segment2d shifted = new Segment2d(
                    segment.start().toVector().add(startShift).round(),
                    segment.end().toVector().add(endShift).round()
                );

                // Don't draw lines in reverse direction
                if (shifted.length() > 0 && shifted.direction().x() * segment.direction().x() >= 0 && shifted.direction().y() * segment.direction().y() >= 0)
                    iterator = new ConnectedLine2dIterator(shifted);
            }
            index++;
        }
    }

    @Override
    public boolean hasNext() {
        prepare();
        return iterator != null && iterator.hasNext();
    }

    @Override
    public Positioned2d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return iterator.next();
    }
}

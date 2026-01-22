package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;

/**
 * An iterator over voxels of a {@link Line2d} with thickness.
 */
 public class ThickLineSting2dIndexedIterator implements Iterator<IndexedPosition2d> {
    private final LineString2d lineString;
    private final double thickness;

    private double xOffset = 0.0;
    private int index = 0;
    private Iterator<Positioned2d> lineIterator = null;

    /**
     * Creates a new lineString iterator whith thickness.
     *
     * @param lineString the lineString to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineSting2dIndexedIterator(LineString2d lineString, double thickness) {
        this.lineString = lineString;
        this.thickness = thickness;
    }

    private void prepare() {
        while ((lineIterator == null || !lineIterator.hasNext()) && index < lineString.size()) {
            if (index > 0)
                xOffset += lineString.get(index - 1).length();

            Line2d line = lineString.get(index);
            Vector2d normal = line.direction().normal();

            // Normal should never be zero but it costs nothing to check
            if (!normal.isZero()) {
                Line2d next = lineString.get(index + 1);
                Line2d previous = lineString.get(index - 1);

                Vector2d startBevelDirection = previous == null ? normal : normal.add(previous.direction().normal());
                Vector2d endBevelDirection = next == null ? normal.opposite() : normal.add(next.direction().normal()).opposite();

                lineIterator = new ThickLine2dIterator(line, thickness, startBevelDirection, endBevelDirection);
            }
            index++;
        }
    }

    @Override
    public boolean hasNext() {
        prepare();
        return lineIterator != null && lineIterator.hasNext();
    }

    @Override
    public IndexedPosition2d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Positioned2d position = lineIterator.next();
        Line2d line = lineString.get(index);
        Vector2d relative = line.convertLineRelative(position.coords());

        return new IndexedPosition2d(position.coords(), new Vector2d(xOffset + relative.x(), relative.y()), line);
    }
}

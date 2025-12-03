package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Segment3d;

/**
 * An iterator over voxels of a {@link LineString3d} with thickness.
 * <p>
 * This iterator is not an exact transposition of 2d iterator to 3d.
 * Actually, on Z axis, it behaves like a "thin" iterator (voxels are not connected along Z-axis).
 */
public class ThickLineString2d5Iterator implements Iterator<Positioned3d> {

    private final LineString3d lineString;
    private final double thickness;

    private int index = -1;
    private Iterator<Positioned3d> lineIterator = null;

    /**
     * Creates a new lineString iterator whith thickness.
     *
     * @param lineString the lineString to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineString2d5Iterator(LineString3d lineString, double thickness) {
        this.lineString = lineString;
        this.thickness = thickness;
    }

    private Vector2d computeBevelDirection(Segment3d segment, Vector2d normal) {
        if (segment == null)
            return normal;

        Vector2d direction = normal.add(segment.direction().to2d().normal());

        // In case of other segment in perfect opposite direction:
        if (direction.isZero())
            return normal;
        return direction;
    }

    private void prepare() {
        while ((lineIterator == null || !lineIterator.hasNext()) && index < lineString.size() - 1) {
            index++;
            Segment3d segment = getCurrentSegment();
            Vector2d direction = segment.direction().to2d();

            // Discard pure vertical lines
            if (!direction.isZero()) {
                Segment3d next = lineString.get(index + 1);
                Segment3d previous = lineString.get(index - 1);
                Vector2d normal = direction.normal();

                lineIterator = new ThickSegment2d5Iterator(segment, thickness,
                    computeBevelDirection(previous, normal),
                    computeBevelDirection(next, normal).opposite());
            }
        }
    }

    /**
     * {@return the {@link Segment3d} currently iterating on}
     */
    public Segment3d getCurrentSegment() {
        return lineString.get(index);
    }

    @Override
    public boolean hasNext() {
        prepare();
        return lineIterator != null && lineIterator.hasNext();
    }

    @Override
    public Positioned3d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return lineIterator.next();
    }
}

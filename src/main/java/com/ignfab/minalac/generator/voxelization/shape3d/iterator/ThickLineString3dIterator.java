package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;

// Cet itérateur n'est pas tout à fait le pendant de celui en 2d.
// En Z, il se contente de faire du "thin" (voxels non connectés en Z)

/**
 * An iterator over voxels of a {@link Line3d} with thickness.
 */
public class ThickLineString3dIterator implements Iterator<Positioned3d> {

    private final LineString3d lineString;
    private final double thickness;

    private int index;
    private ThickLine3dIterator iterator;

    /**
     * Creates a new lineString iterator whith thickness.
     *
     * @param lineString the lineString to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineString3dIterator(LineString3d lineString, double thickness) {
        this.lineString = lineString;
        this.thickness = thickness;
        index = 0;
        iterator = null;
    }

    private void prepare() {
        while ((iterator == null || !iterator.hasNext()) && index < lineString.size()) {
            Line3d line = lineString.get(index);
            Vector2d normal = line.direction().to2d().normal();

            // Pure vertical lines
            if (!normal.isZero()) {
                Line3d next = lineString.get(index + 1);
                Line3d previous = lineString.get(index - 1);

                // Previous & next horizontal direction may be zero (pure vertical line) but anyway, that will work
                Vector2d startBevelDirection = previous == null ? normal : normal.add(previous.direction().to2d().normal());
                Vector2d endBevelDirection = next == null ? normal.opposite() : normal.add(next.direction().to2d().normal()).opposite();

                iterator = new ThickLine3dIterator(line, thickness, startBevelDirection, endBevelDirection);
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
    public Positioned3d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return iterator.next();
    }
}

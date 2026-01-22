package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;

// Cet itérateur n'est pas tout à fait le pendant de celui en 2d.
// En Z, il se contente de faire du "thin" (voxels non connectés en Z)

/**
 * An iterator over voxels of a {@link Line3d} with thickness.
 */
public class ThickLineString3dIndexedIterator implements Iterator<Indexed2dPosition3d> {

    private final LineString3d lineString;
    private final double thickness;

    private double xOffset = 0.0;
    private Line2d currentLine = null;

    private int index = 0;
    private Iterator<Positioned3d> lineIterator = null;

    /**
     * Creates a new lineString iterator whith thickness.
     *
     * @param lineString the lineString to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineString3dIndexedIterator(LineString3d lineString, double thickness) {
        this.lineString = lineString;
        this.thickness = thickness;
    }

    private Vector2d computeBevelDirection(Line3d line, Vector2d normal) {
        if (line == null)
            return normal;
        Vector2d direction = normal.add(line.direction().to2d().normal());
        // In case of other line in perfect opposite direction:
        if (direction.isZero())
            return normal;
        return direction;
    }

    private void prepare() {
        while ((lineIterator == null || !lineIterator.hasNext()) && index < lineString.size()) {
            if (currentLine != null)
                xOffset += currentLine.length();

            Line3d line = lineString.get(index);
            Vector2d normal = line.direction().to2d().normal();

            // Discard pure vertical lines
            if (!normal.isZero()) {
                Line3d next = lineString.get(index + 1);
                Line3d previous = lineString.get(index - 1);

                lineIterator = new ThickLine3dIterator(line, thickness,
                    computeBevelDirection(previous, normal),
                    computeBevelDirection(next, normal).opposite()
                );
                currentLine = line.to2d();
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
    public Indexed2dPosition3d next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Positioned3d position = lineIterator.next();
        Vector2d relative = currentLine.convertLineRelative(position.coords().to2d());
        return new Indexed2dPosition3d(position.coords(), new Vector2d(xOffset + relative.x(), relative.y()), currentLine);
    }
}

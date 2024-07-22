package com.ignfab.minalac.generator.utils.shape2d.iterator;

import com.ignfab.minalac.generator.utils.shape2d.Polygon2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position inside a 2d polygon.
 * It produces {@link Voxel2d} like this:
 * <pre>{@code
 *  for (int y = polygon.bbox().getMinY(); y <= polygon.bbox().getMaxY(); y++) {
 *      List<Double> intersections = polygon.intersections(y);
 *      if (intersections.isEmpty())
 *          continue;
 *      if (intersections.size() % 2 != 0)
 *          throw new IllegalStateException("Unclosed polygon"); // Should never happen
 *      for (int i = 0; i < intersections.size(); i += 2) {
 *          int enter = (int) Math.round(intersections.get(i));
 *          int exit = (int) Math.round(intersections.get(i + 1));
 *          for (int x = enter; x < exit; x++)
 *              yield new Voxel2d.Impl(new WorldCoords2d(x, y)); // Each call to .next() will return this
 *      }
 *  }
 * }</pre>
 *
 * @see Polygon2d
 */
public class Polygon2dIterator implements Iterator<Voxel2d> {
    private final Polygon2d polygon;
    private int x;
    private int y;
    private List<Double> intersections;
    private int i;
    private boolean computeX;
    private WorldCoords2d current;

    /**
     * Creates a new iterator on the given polygon.
     *
     * @param polygon the polygon to iterate over.
     */
    public Polygon2dIterator(Polygon2d polygon) {
        this.polygon = polygon;
        if (polygon.bbox().isEmpty())
            current = null;
        else {
            y = polygon.bbox().getMinY();
            computeIntersections(false);
            computeX = true;
            moveOn();
        }
    }

    private boolean computeIntersections(boolean incrementY) {
        if (incrementY) {
            y++;
            if (y > polygon.bbox().getMaxY())
                return false;
        }
        intersections = polygon.intersections(y);
        if (intersections.isEmpty())
            return computeIntersections(true);
        if (intersections.size() % 2 != 0)
            throw new IllegalStateException("Unclosed polygon"); // Should never happen
        i = 0;
        return true;
    }

    private void moveOn() {
        if (computeX) {
            x = (int) Math.round(intersections.get(i));
            computeX = false;
        } else
            x++;

        int exit = (int) Math.round(intersections.get(i + 1));
        if (x >= exit) {
            i += 2;
            computeX = true;

            if (i >= intersections.size()) {
                if (!computeIntersections(true)) {
                    current = null;
                    return; // Unable to compute intersections => end of iteration
                }
            }
            moveOn();
            return;
        }

        current = new WorldCoords2d(x, y);
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public Voxel2d next() {
        if (current == null)
            throw new NoSuchElementException();
        Voxel2d element = new Voxel2d.Impl(current);
        moveOn();
        return element;
    }
}

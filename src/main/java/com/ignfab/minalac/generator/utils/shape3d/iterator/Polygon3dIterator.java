package com.ignfab.minalac.generator.utils.shape3d.iterator;

import com.ignfab.minalac.generator.utils.shape3d.Polygon3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position inside a 3d polygon.
 * It produces {@link Voxel3d} like this:
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
 *              yield new Voxel3d.Impl(new WorldCoords3d(x, y, 0)); // Each call to .next() will return this
 *      }
 *  }
 * }</pre>
 * As you can see, it does not produce a meaningful Z value for now...
 *
 * @see Polygon3d
 */
public class Polygon3dIterator implements Iterator<Voxel3d> {
    private final Polygon3d polygon;
    private int x;
    private int y;
    private List<Double> intersections;
    private int i;
    private boolean computeX;
    private WorldCoords3d current;

    /**
     * Creates a new iterator on the given polygon.
     *
     * @param polygon the polygon to iterate over.
     */
    public Polygon3dIterator(Polygon3d polygon) {
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

        current = new WorldCoords3d(x, y, 0); // TODO Interpolate Z value instead of 0
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public Voxel3d next() {
        if (current == null)
            throw new NoSuchElementException();
        Voxel3d element = new Voxel3d.Impl(current);
        moveOn();
        return element;
    }
}

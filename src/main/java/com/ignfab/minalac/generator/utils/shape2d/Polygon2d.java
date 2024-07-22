package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.shape2d.iterator.Polygon2dIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a 2d polygon with holes in the voxel world.
 * It consists of an outer shell (polyline) and inside holes (collection of polyline).
 * Both shell and all holes must be closed polyline,
 * and all holes must be contained inside the shell.
 */
public class Polygon2d implements Iterable<Voxel2d> {
    private final PolyLine2d shell;
    private final Collection<PolyLine2d> holes;
    private final WorldBBox2d bbox;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon2d(PolyLine2d shell, Collection<PolyLine2d> holes) {
        this.shell = shell;
        this.holes = holes;
        bbox = shell.bbox();
    }

    /**
     * Computes intersections of this polygon at a given y.
     * Very useful for voxelization purpose.
     * <p>
     * The intersection are computed between lines of this polygon
     * and a line segment from ({@code bbox.getMinX()}, {@code y + 0.5})
     * to ({@code bbox.getMaxX() + 1}, {@code y + 0.5}).
     *
     * @param y the y-component value of the straight line to intersect.
     * @return the list of intersections sorted from lower to higher.
     */
    public List<Double> intersections(int y) {
        if (y < bbox.getMinY() || y > bbox.getMaxY())
            return Collections.emptyList();
        List<Double> xValues = new ArrayList<>();
        for (Line2d line : borders()) {
            Double inter = intersection(y, line);
            if (inter != null)
                xValues.add(inter);
        }
        xValues.sort(null);
        return xValues;
    }

    private static Double intersection(int y, Line2d line) {
        int startMilliY = line.start().milliY();
        int endMilliY = line.end().milliY();
        int milliY = y * 1000 + 500;
        if ((startMilliY < milliY && endMilliY < milliY) || (startMilliY > milliY && endMilliY > milliY))
            return null;
        if (Math.abs(line.directionY()) < 1e-4)
            return null;
        double t = (y + 0.5 - line.originY()) / line.directionY();
        return line.atIndex(t).realX();
    }

    /**
     * Returns the bounding box of this polygon.
     * This is the smallest box containing all the lines.
     *
     * @return the bounding box of this polygon.
     */
    public WorldBBox2d bbox() {
        return bbox;
    }

    /**
     * Returns a new iterable over voxels on the edge of this polygon.
     *
     * @return the border iterable of this polygon.
     */
    public Iterable<Line2d> borders() {
        return () -> MultiIterator.concat(shell.lines(), () -> new MultiIterator<>(new RemapIterator<>(holes, PolyLine2d::lines)));
    }

    /**
     * Returns a new iterator over voxels inside this polygon.
     *
     * @return a new {@link Polygon2dIterator} on this polygon.
     */
    @Override
    public Polygon2dIterator iterator() {
        return new Polygon2dIterator(this);
    }
}

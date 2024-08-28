package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.UnionIterator;
import com.ignfab.minalac.generator.utils.iterator.UnwrapIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.shape2d.iterator.Polygon2dIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.ArrayList;
import java.util.Arrays;
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
     * Creates a new polygon with the given shell and holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the holes of the polygon.
     */
    public Polygon2d(PolyLine2d shell, PolyLine2d... holes) {
        this(shell, Arrays.asList(holes));
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
    public Iterable<IndexedVoxel2d> borders() {
        return () -> new UnionIterator<>(
            shell.iterator(),
            new UnwrapIterator<>(holes));
    }

    /**
     * Returns a new iterator over all voxels of the polygon.
     *
     * @return a new {@link Polygon2dIterator} on this polygon.
     */
    @Override
    public Polygon2dIterator iterator() {
        return new Polygon2dIterator(this, true);
    }

    /**
     * Returns a new iterable over voxels strictly inside polygon.
     *
     * @return the inside iterable of this polygon.
     */
    public Iterable<Voxel2d> inside() {
        return () -> new Polygon2dIterator(this, false);
    }

    /**
     * Returns a new iterable over all lines (shell and holes) of this polygon.
     *
     * @return iterator over lines
     */
    public Iterable<Line2d> lines() {
        return () -> new UnionIterator<>(
            new UnwrapIterator<>(new RemapIterator<>(holes, PolyLine2d::lines)),
            shell.lines().iterator());
    }
    /**
     * Lists intersections of this polygon at a given y.
     * Very useful for voxelization purpose.
     * <p>
     * {@code Line2d} computes intersection of a line at that given y.
     * This method lists all these intersection.
     *
     * @param y the y-component value of the straight line to intersect.
     * @return the list of intersections.
     */
    public List<Line2d.Intersection> intersections(int y) {
        if (y < bbox.getMinY() || y > bbox.getMaxY())
            return Collections.emptyList();

        List<Line2d.Intersection> intersections = new ArrayList<>();
        for (Line2d line : lines()) {
            Line2d.Intersection inter = line.intersection(y);
            if (inter != null)
                intersections.add(inter);
        }

        return intersections;
    }
}


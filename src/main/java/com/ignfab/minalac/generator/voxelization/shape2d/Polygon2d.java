package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Polygon2dIterator;

/**
 * Represents a 2d polygon with holes in the voxel world.
 * It consists of an outer shell (polyline) and inside holes (collection of polyline).
 * Both shell and all holes must be closed polyline,
 * and all holes must be contained inside the shell.
 */
public class Polygon2d implements Bounded2d, Shape2d {
    private final Collection<Line2d> lines;
    private final WorldBBox2d bbox;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon2d(Polyline2d shell, Collection<Polyline2d> holes) {
        bbox = shell.bbox();

        // We only need to know about lines (shell and holes are supposed to be
        // closed)
        lines = new LinkedList<>();
        lines.addAll(shell.lines());
        for (Polyline2d hole : holes)
            lines.addAll(hole.lines());
    }

    /**
     * Creates a new polygon with the given shell and holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the holes of the polygon.
     */
    public Polygon2d(Polyline2d shell, Polyline2d... holes) {
        this(shell, Arrays.asList(holes));
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
        if (y < bbox.minY() || y > bbox.maxY())
            return Collections.emptyList();

        List<Line2d.Intersection> intersections = new ArrayList<>();
        for (Line2d line : lines) {
            Line2d.Intersection intersection = line.intersection(y);
            if (intersection != null)
                intersections.add(intersection);
        }

        return intersections;
    }

    /**
     * Returns a new iterable over voxels on the edge of this polygon.
     *
     * @return the border iterable of this polygon.
     */
    public Iterable<Line2d> lines() {
        return lines;
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public Iterable<LineVoxel2d> borderVoxels() {
        return Iterables.unwrap(Iterables.remap(lines, Shape2d::borderVoxels));
    }

    @Override
    public Iterable<Positioned2d> insideVoxels() {
        return () -> new Polygon2dIterator(this, false);
    }

    @Override
    public Iterable<Positioned2d> allVoxels() {
        return () -> new Polygon2dIterator(this, true);
    }
}


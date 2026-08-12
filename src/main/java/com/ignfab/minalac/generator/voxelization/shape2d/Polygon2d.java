package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A 2d polygon with holes in the voxel world.
 * It consists of an outer shell (linear ring) and inside holes (collection of linear rings).
 * Holes must be contained in shell and must not overlap each other (not checked).
 */

public class Polygon2d implements Shape2d {
    private final LinearRing2d shell;
    private final Collection<LinearRing2d> holes;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon2d(LinearRing2d shell, Collection<LinearRing2d> holes) {
        this.shell = shell.toClockwise();
        this.holes = new ArrayList<>(holes.size());
        for (LinearRing2d hole : holes)
            this.holes.add(hole.toCounterClockwise());
    }

    /**
     * Creates a new polygon with the given shell and holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the holes of the polygon.
     */
    public Polygon2d(LinearRing2d shell, LinearRing2d... holes) {
        this(shell, Arrays.asList(holes));
    }

    public LinearRing2d shell() {
        return shell;
    }

    @Override
    public WorldBBox2d bbox() {
        return shell.bbox();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[shell=%s, holes=%s]".formatted(shell, holes);
    }

    @Override
    public Iterable<Point2d> points() {
        return Iterables.flatMap(lineStrings(), LineString2d::points);
    }

    /**
     * {@return iterable over all segments in the shape}
     */
    public Iterable<Segment2d> segments() {
        return Iterables.flatMap(lineStrings(), LineString2d::segments);
    }

    @Override
    public Iterable<LineString2d> lineStrings() {
        return Iterables.union(shell.lineStrings(), holes);
    }

    @Override
    public Iterable<Polygon2d> polygons() {
        return Iterables.singleton(this);
    }
}

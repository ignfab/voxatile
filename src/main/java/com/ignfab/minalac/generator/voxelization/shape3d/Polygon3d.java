package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Arrays;
import java.util.Collection;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Represents a 3d polygon with holes in the voxel world.
 * It consists of an outer shell (polyline) and inside holes (collection of polyline).
 * Both shell and all holes must be closed polyline,
 * and all holes must be contained inside the shell.
 */
public class Polygon3d implements Shape3d {
    private final LinearRing3d shell;
    private final Collection<LinearRing3d> holes;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon3d(LinearRing3d shell, Collection<LinearRing3d> holes) {
        this.shell = shell;
        this.holes = holes;
    }

    /**
     * Creates a new polygon with the given shell and holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the holes of the polygon.
     */
    public Polygon3d(LinearRing3d shell, LinearRing3d... holes) {
        this(shell, Arrays.asList(holes));
    }

    /**
     * {@return all segments of the polygon}
     */
    public Iterable<Segment3d> segments() {
        return Iterables.unwrap(Iterables.remap(lineStrings(), LineString3d::segments));
    }

    @Override
    public WorldBBox3d bbox() {
        return shell.bbox();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[shell=%s, holes=%s]".formatted(shell, holes);
    }

    @Override
    public Iterable<Point3d> points() {
        return Iterables.unwrap(Iterables.remap(lineStrings(), LineString3d::points));
    }


    @Override
    public Iterable<LineString3d> lineStrings() {
        return Iterables.union(shell.lineStrings(), holes);
    }

    @Override
    public Iterable<Polygon3d> polygons() {
        return Iterables.singleton(this);
    }
}

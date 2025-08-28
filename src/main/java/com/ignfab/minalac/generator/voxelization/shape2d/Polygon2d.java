package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A 2d polygon with holes in the voxel world.
 * It consists of an outer shell (linear ring) and inside holes (collection of linear rings).
 * Holes must be contained in shell and must not overlap each other (not checked).
 */

public class Polygon2d implements Bounded2d, Shape2d {
    private final LinearRing2d shell;
    private final Collection<LinearRing2d> holes;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon2d(LinearRing2d shell, Collection<LinearRing2d> holes) {
        this.shell = shell;
        this.holes = holes;
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

    @Override
    public WorldBBox2d bbox() {
        return shell.bbox();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{shell=%s, holes=%s}".formatted(shell, String.join(", ", holes.stream().map(Object::toString).toList()));
    }

    // Shape2d implementation

    @Override
    public Iterable<Line2d> lines() {
        return Iterables.unwrap(Iterables.remap(lineStrings(), LineString2d::lines));
    }

    @Override
    public Iterable<LineString2d> lineStrings() {
        return Iterables.union(Collections.singleton(shell), holes);
    }

    @Override
    public Iterable<Polygon2d> polygons() {
        return Collections.singleton(this);
    }
}

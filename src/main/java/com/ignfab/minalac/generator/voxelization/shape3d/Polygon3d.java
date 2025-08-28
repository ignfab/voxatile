package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Collection;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Represents a 3d polygon with holes in the voxel world.
 * It consists of an outer shell (polyline) and inside holes (collection of polyline).
 * Both shell and all holes must be closed polyline,
 * and all holes must be contained inside the shell.
 */
public class Polygon3d implements Bounded3d, Shape3d {
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
     * Returns a new iterable over voxels on the edge of this polygon.
     *
     * @return the border iterable of this polygon.
     */
    public Iterable<Line3d> lines() {
        return Iterables.union(shell.lines(), Iterables.unwrap(Iterables.remap(holes, LinearRing3d::lines)));
    }

    @Override
    public WorldBBox3d bbox() {
        return shell.bbox();
    }
}

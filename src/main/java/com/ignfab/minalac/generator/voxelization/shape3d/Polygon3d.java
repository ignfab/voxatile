package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a 3d polygon with holes in the voxel world.
 * It consists of an outer shell (polyline) and inside holes (collection of polyline).
 * Both shell and all holes must be closed polyline,
 * and all holes must be contained inside the shell.
 */
public class Polygon3d implements Bounded3d, Shape3d {
    private final Collection<Line3d> lines;
    private final WorldBBox3d bbox;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon3d(Polyline3d shell, Collection<Polyline3d> holes) {
        bbox = shell.bbox();

        // We only need to know about lines (shell and holes are supposed to be
        // closed)
        lines = new LinkedList<>();
        lines.addAll(shell.lines());
        for (Polyline3d hole : holes)
            lines.addAll(hole.lines());
    }

    /**
     * Returns a new iterable over voxels on the edge of this polygon.
     *
     * @return the border iterable of this polygon.
     */
    public Iterable<Line3d> lines() {
        return lines;
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public Iterable<LineVoxel3d> borderVoxels() {
        return Iterables.unwrap(Iterables.remap(lines, Shape3d::borderVoxels));
    }

    @Override
    public Iterable<LineVoxel3d> connectedBorderVoxels() {
        return Iterables.unwrap(Iterables.remap(lines, Shape3d::connectedBorderVoxels));
    }

    @Override
    public Iterable<Positioned3d> insideVoxels() {
        throw new UnsupportedOperationException("Unimplemented method 'insideVoxels'");
    }

    @Override
    public Iterable<Positioned3d> allVoxels() {
        throw new UnsupportedOperationException("Unimplemented method 'allVoxels'");
    }
}

package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

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
        this.bbox = shell.bbox();

        // We only need to know about lines (shell and holes are supposed to be
        // closed)
        this.lines = new LinkedList<Line3d>();
        for (Line3d line : shell.lines())
            this.lines.add(line);
        for (Polyline3d hole : holes)
            for (Line3d line : hole.lines())
                this.lines.add(line);
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
        return () -> new MultiIterator<>(new RemapIterator<>(lines, Shape3d::borderVoxels));
    }

    @Override
    public Iterable<Voxel3d> insideVoxels() {
        throw new UnsupportedOperationException("Unimplemented method 'insideVoxels'");
    }

    @Override
    public Iterable<Voxel3d> allVoxels() {
        throw new UnsupportedOperationException("Unimplemented method 'allVoxels'");
    }
}

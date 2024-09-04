package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.shape3d.iterator.Polygon3dIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Represents a 3d polygon with holes in the voxel world.
 * It consists of an outer shell (polyline) and inside holes (collection of polyline).
 * Both shell and all holes must be closed polyline,
 * and all holes must be contained inside the shell.
 */
public class Polygon3d implements Shape3d {
    private final PolyLine3d shell;
    private final Collection<PolyLine3d> holes;
    private final WorldBBox3d bbox;

    /**
     * Creates a new polygon with the given shell and collection of holes.
     *
     * @param shell the outer shell of the polygon.
     * @param holes the collection of holes of the polygon.
     */
    public Polygon3d(PolyLine3d shell, Collection<PolyLine3d> holes) {
        this.shell = shell;
        this.holes = holes;
        bbox = shell.bbox();
    }

    /**
     * Returns the bounding box of this polygon.
     * This is the smallest box containing all the lines.
     *
     * @return the bounding box of this polygon.
     */
    public WorldBBox3d bbox() {
        return bbox;
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
    public List<Line3d.Intersection> intersections(int y) {
        if (y < bbox.getMinY() || y > bbox.getMaxY())
            return Collections.emptyList();

        List<Line3d.Intersection> intersections = new ArrayList<>();
        for (Line3d line : lines()) {
            Line3d.Intersection inter = line.intersection(y);
            if (inter != null)
                intersections.add(inter);
        }

        return intersections;
    }

    /**
     * Returns a new iterable over voxels on the edge of this polygon.
     *
     * @return the border iterable of this polygon.
     */
    public Iterable<Line3d> lines() {
        return () -> MultiIterator.concat(
            shell.lines(),
            () -> new MultiIterator<>(new RemapIterator<>(holes, PolyLine3d::lines))
        );
    }

    @Override
    public Iterable<LineVoxel3d> borderVoxels() {
        return () -> new MultiIterator<>(new RemapIterator<>(lines(), Shape3d::borderVoxels));
    }

    @Override
    public Iterable<Voxel3d> insideVoxels() {
        return () -> new Polygon3dIterator(this, false);
    }

    @Override
    public Iterable<Voxel3d> allVoxels() {
        return () -> new Polygon3dIterator(this, true);
    }
}

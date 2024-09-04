package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.shape2d.iterator.Polygon2dIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
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
public class Polygon2d implements Shape2d {
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

    /**
     * Returns a new iterable over voxels on the edge of this polygon.
     *
     * @return the border iterable of this polygon.
     */
    public Iterable<Line2d> lines() {
        return () -> MultiIterator.concat(
            shell.lines(),
            () -> new MultiIterator<>(new RemapIterator<>(holes, PolyLine2d::lines))
        );
    }

    @Override
    public Iterable<LineVoxel2d> borderVoxels() {
        return () -> new MultiIterator<>(new RemapIterator<>(lines(), Shape2d::borderVoxels));
    }

    @Override
    public Iterable<Voxel2d> insideVoxels() {
        return () -> new Polygon2dIterator(this, false);
    }

    @Override
    public Iterable<Voxel2d> allVoxels() {
        return () -> new Polygon2dIterator(this, true);
    }
}


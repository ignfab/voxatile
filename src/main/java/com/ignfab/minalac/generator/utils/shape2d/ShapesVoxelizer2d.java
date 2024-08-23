package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.BoundedIterator2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A voxelizer based on 2d shapes.
 */
public class ShapesVoxelizer2d implements Voxelizer2d {
    private final WorldBBox2d bbox;
    private final List<Point2d> points = new ArrayList<>();
    private final List<PolyLine2d> lines = new ArrayList<>();
    private final List<Polygon2d> polygons = new ArrayList<>();

    /**
     * Creates a new voxelizer with the given limits.
     *
     * @param bbox the limits of the returned voxels.
     */
    public ShapesVoxelizer2d(WorldBBox2d bbox) {
        this.bbox = bbox;
    }

    /**
     * Adds a point to the stored shapes.
     *
     * @param point the point to add.
     */
    public void addPoint(Point2d point) {
        points.add(point);
    }

    /**
     * Adds a polyline to the stored shapes.
     *
     * @param line the line to add.
     */
    public void addLine(PolyLine2d line) {
        lines.add(line);
    }

    /**
     * Adds a polygon to the stored shapes.
     *
     * @param polygon the polygon to add.
     */
    public void addPolygon(Polygon2d polygon) {
        polygons.add(polygon);
    }

    /**
     * Returns an iterator over all voxels in all shapes stored in this voxelizer.
     *
     * @return the global iterator of all shapes.
     */
    @Override
    public Iterator<Voxel2d> iterator() {
        return new BoundedIterator2d<>(MultiIterator.concat(
            () -> new MultiIterator<>(points),
            () -> new MultiIterator<>(lines),
            () -> new MultiIterator<>(polygons)
        ), bbox);
    }

    /**
     * Returns an iterable over border voxels on all shapes stored in this voxelizer.
     * Polyline and point are considered to be borders.
     *
     * @return the border iterable of all shapes.
     */
    @Override
    public Iterable<IndexedVoxel2d> borders() {
        return () -> new BoundedIterator2d<>(MultiIterator.concat(
            () -> new MultiIterator<>(points),
            () -> new MultiIterator<>(lines),
            () -> new MultiIterator<>(new MultiIterator<>(new RemapIterator<>(polygons, Polygon2d::borders)))
        ), bbox);
    }

    /**
     * Returns an iterable over inside voxel on all shapes stored in this voxelizer.
     * Only polygon shapes have inside voxels.
     *
     * @return the inside iterable of all shapes.
     */
    @Override
    public Iterable<Voxel2d> inside() {
        return () -> new BoundedIterator2d<>(new MultiIterator<>(new RemapIterator<>(polygons, Polygon2d::inside)), bbox);
    }
}

package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.iterator.BoundedIterator3d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A voxelizer based on 3d shapes.
 */
public class ShapesVoxelizer3d implements Voxelizer3d {
    private final WorldBBox3d bbox;
    private final List<Point3d> points = new ArrayList<>();
    private final List<PolyLine3d> lines = new ArrayList<>();
    private final List<Polygon3d> polygons = new ArrayList<>();

    /**
     * Creates a new voxelizer with the given limits.
     *
     * @param bbox the limits of the returned voxels.
     */
    public ShapesVoxelizer3d(WorldBBox3d bbox) {
        this.bbox = bbox;
    }

    /**
     * Adds a point to the stored shapes.
     *
     * @param point the point to add.
     */
    public void addPoint(Point3d point) {
        points.add(point);
    }

    /**
     * Adds a polyline to the stored shapes.
     *
     * @param line the line to add.
     */
    public void addLine(PolyLine3d line) {
        lines.add(line);
    }

    /**
     * Adds a polygon to the stored shapes.
     *
     * @param polygon the polygon to add.
     */
    public void addPolygon(Polygon3d polygon) {
        polygons.add(polygon);
    }

    /**
     * Returns an iterator over all voxels in all shapes stored in this voxelizer.
     *
     * @return the global iterator of all shapes.
     */
    @Override
    public Iterator<Voxel3d> iterator() {
        return new BoundedIterator3d<>(MultiIterator.concat(
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
    public Iterable<IndexedVoxel3d> borders() {
        return () -> new BoundedIterator3d<>(MultiIterator.concat(
            () -> new MultiIterator<>(points),
            () -> new MultiIterator<>(lines),
            () -> new MultiIterator<>(new MultiIterator<>(new RemapIterator<>(polygons, Polygon3d::borders)))
        ), bbox);
    }
}

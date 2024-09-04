package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.iterator.BoundedIterator3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
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
    private final List<Shape3d> shapes = new ArrayList<>();

    /**
     * Creates a new voxelizer with the given limits.
     *
     * @param bbox the limits of the returned voxels.
     */
    public ShapesVoxelizer3d(WorldBBox3d bbox) {
        this.bbox = bbox;
    }

    /**
     * Adds a shape to the stored shapes.
     *
     * @param shape the shape to add.
     */
    public void addShape(Shape3d shape) {
        shapes.add(shape);
    }

    @Override
    public Iterable<LineVoxel3d> borders() {
        return () -> new BoundedIterator3d<>(
            new MultiIterator<>(
                new RemapIterator<>(shapes, Shape3d::borderVoxels)),
            bbox);
    }

    @Override
    public Iterator<Voxel3d> iterator() {
        return new BoundedIterator3d<>(
            new MultiIterator<>(
                new RemapIterator<>(shapes, Shape3d::allVoxels)),
            bbox);
    }
}

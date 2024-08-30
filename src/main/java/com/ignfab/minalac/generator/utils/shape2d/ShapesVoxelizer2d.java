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
    private final List<Shape2d> shapes = new ArrayList<>();

    /**
     * Creates a new voxelizer with the given limits.
     *
     * @param bbox the limits of the returned voxels.
     */
    public ShapesVoxelizer2d(WorldBBox2d bbox) {
        this.bbox = bbox;
    }

    /**
     * Adds a shape to this voxelizer.
     *
     * @param shape the shape to add.
     */
    public void addShape(Shape2d shape) {
        shapes.add(shape);
    }

    /**
     * Returns an iterator over all voxels in all shapes stored in this voxelizer.
     *
     * @return the global iterator of all shapes.
     */
    @Override
    public Iterator<Voxel2d> iterator() {
        return new BoundedIterator2d<>(new MultiIterator<>(shapes), bbox);
    }

    /**
     * Returns an iterable over border voxels on all shapes stored in this voxelizer.
     * Polyline and point are considered to be borders.
     *
     * @return the border iterable of all shapes.
     */
    @Override
    public Iterable<IndexedVoxel2d> borders() {
        return () -> new BoundedIterator2d<>(new MultiIterator<>(new RemapIterator<>(shapes, Shape2d::borderVoxels)), bbox);
    }

    /**
     * Returns an iterable over inside voxel on all shapes stored in this voxelizer.
     * Only polygon shapes have inside voxels.
     *
     * @return the inside iterable of all shapes.
     */
    @Override
    public Iterable<Voxel2d> inside() {
        return () -> new BoundedIterator2d<>(new MultiIterator<>(new RemapIterator<>(shapes, Shape2d::insideVoxels)), bbox);
    }
}

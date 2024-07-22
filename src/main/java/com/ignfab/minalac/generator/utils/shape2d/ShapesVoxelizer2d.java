package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.BoundedIterator2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
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
     * Adds a shape to the stored shapes.
     *
     * @param shape the shape to add.
     */
    public void addShape(Shape2d shape) {
        shapes.add(shape);
    }

    @Override
    public Iterable<LineVoxel2d> borders() {
        return () -> new BoundedIterator2d<>(
            new MultiIterator<>(
                new RemapIterator<>(shapes, Shape2d::borderVoxels)
            ),
            bbox
        );
    }

    @Override
    public Iterable<Voxel2d> inside() {
        return () -> new BoundedIterator2d<>(
            new MultiIterator<>(
                new RemapIterator<>(shapes, Shape2d::insideVoxels)
            ),
            bbox
        );
    }

    @Override
    public Iterator<Voxel2d> iterator() {
        return new BoundedIterator2d<>(
            new MultiIterator<>(
                new RemapIterator<>(shapes, Shape2d::allVoxels)
            ),
            bbox
        );
    }
}

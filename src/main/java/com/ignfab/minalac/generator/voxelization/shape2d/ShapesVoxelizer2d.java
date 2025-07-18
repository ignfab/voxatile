package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

/**
 * A voxelizer based on 2d shapes.
 */
public class ShapesVoxelizer2d implements Voxelizer2d {
    private final WorldBBox2d bbox;
    private final List<Shape2d> shapes;

    /**
     * Creates a new voxelizer with the given limits.
     *
     * @param bbox the limits of the returned voxels.
     */
    public ShapesVoxelizer2d(WorldBBox2d bbox) {
        this.bbox = bbox;
        shapes = new ArrayList<>();
    }

    // Constructor for empty voxelizer
    private ShapesVoxelizer2d() {
        bbox = null;
        shapes = null;
    }

    /**
     * Adds a shape to the stored shapes.
     *
     * @param shape the shape to add.
     */
    public void addShape(Shape2d shape) {
        shapes.add(shape);
    }

    /**
     * Returns an iterable over voxels on the border of shapes.
     * Additional information is given in {@code LineVoxel2d} about border line
     * and position in it.
     *
     * @return an iterable over border voxels
     */
    public Iterable<LineVoxel2d> borders() {
        return () -> bbox.crop(Iterables.unwrap(Iterables.remap(shapes, Shape2d::borderVoxels)));
    }

    /**
     * Returns an iterable over voxels inside shapes (excluding borders).
     *
     * @return an iterable over inside voxels
     */
    public Iterable<Positioned2d> inside() {
        return () -> bbox.crop(Iterables.unwrap(Iterables.remap(shapes, Shape2d::insideVoxels)));
    }

    /**
     * This is the global iterator over all voxels of shapes.
     * This is the only iterator containing Points. These are exluded from
     * {@code inside()} or {@code borders()}.
     *
     * @return iterator over all shapes voxels.
     */
    @Override
    public Iterator<Positioned2d> iterator() {
        return bbox.crop(Iterables.unwrap(Iterables.remap(shapes, Shape2d::allVoxels)));
    }

    // TODO-PR: Temporary. To be removed when this PR is based on PR #113.
    /**
     * Returns the shapes of this voxelizer.
     * @return the {@link Shape2d}
     */
    public List<Shape2d> getShapesTemporary() {
        return shapes == null ? new ArrayList<>() : shapes;
    }

    /**
     * A empty {@code ShapeVoxelizer2d} for conveniance.
     */
    public static final ShapesVoxelizer2d EMPTY = new Empty();

    private static final class Empty extends ShapesVoxelizer2d {
        @Override
        public void addShape(Shape2d shape) {}

        @Override
        public Iterator<Positioned2d> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Iterable<LineVoxel2d> borders() {
            return Collections::emptyIterator;
        }

        @Override
        public Iterable<Positioned2d> inside() {
            return Collections::emptyIterator;
        }
    }
}

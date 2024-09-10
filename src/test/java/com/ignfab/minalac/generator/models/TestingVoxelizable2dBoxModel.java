package com.ignfab.minalac.generator.models;

import java.util.Collections;
import java.util.Iterator;

import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.iterator.FilterIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.BoundedIterator2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

/**
 * A model voxelizable in 2d consisting of a simple rectangle.
 */
public class TestingVoxelizable2dBoxModel extends Model implements Voxelizable2d {
    private WorldBBox2d bbox;

    /**
     * Creates a new model with given bbox.
     *
     * @param bbox rectangle of this model in world
     */
    public TestingVoxelizable2dBoxModel(WorldBBox2d bbox) {
        super();
        this.bbox = bbox;
    }

    @Override
    public Voxelizer2d voxelize2d(WorldBBox2d limits) {
        return new BBoxVoxelizer2d(this.bbox, limits);
    }

    // A test class voxelizing bbox with inside and borders (with no line, and 0 index).
    private class BBoxVoxelizer2d implements Voxelizer2d {
        private final WorldBBox2d box;
        private final WorldBBox2d limits;

        BBoxVoxelizer2d(WorldBBox2d box, WorldBBox2d limits) {
            this.box = box;
            this.limits = limits;
        }

        @Override
        public Iterator<Voxel2d> iterator() {
            return new BoundedIterator2d<>(box.iterator(), limits);
        };

        public Iterable<Voxel2d> inside() {
            if (box.sizeX() < 3 || box.sizeY() < 3)
                return () -> Collections.emptyIterator();

            WorldBBox2d inner = new WorldBBox2d(
                new WorldCoords2d(box.minX() + 1, box.minY() + 1),
                new WorldCoords2d(box.maxX() - 1, box.maxY() - 1)
            );

            return () -> new BoundedIterator2d<>(inner.iterator(), limits);
        }

        public Iterable<LineVoxel2d> borders() {
            return () -> new BoundedIterator2d<>(
                new RemapIterator<WorldCoords2d, LineVoxel2d>(
                    new FilterIterator<>(
                        box.iterator(),
                        pos -> (pos.x() <= box.minX()
                            || pos.x() >= box.maxX()
                            || pos.y() <= box.minY()
                            || pos.y() >= box.maxY()
                        )
                    ),
                    pos -> new LineVoxel2d(pos)),
                limits);
        }
    }
}


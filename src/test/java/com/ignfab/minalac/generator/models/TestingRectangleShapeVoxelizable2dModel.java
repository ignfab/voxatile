package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.coordinates.TestingConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polyline2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * A model voxelizable in 2d consisting of a simple rectangle.
 */
public class TestingRectangleShapeVoxelizable2dModel extends ModelImpl implements ShapesVoxelizable2d {
    private final WorldBBox2d bbox;

    /**
     * Creates a new model with given bbox.
     *
     * @param bbox rectangle of this model in world
     */
    public TestingRectangleShapeVoxelizable2dModel(WorldBBox2d bbox) {
        super(TestingConverter.UNUSED);
        this.bbox = bbox;
    }

    @Override
    public ShapesVoxelizer2d voxelize2d(WorldBBox2d limits) {
        ShapesVoxelizer2d voxelizer = new ShapesVoxelizer2d(limits);
        voxelizer.addShape(
            new Polygon2d(
                Polyline2d.fromPoints(
                    bbox.min(),
                    new WorldCoords2d(bbox.minX(), bbox.maxY()),
                    bbox.max(),
                    new WorldCoords2d(bbox.maxX(), bbox.minY()),
                    bbox.min()
                )
            )
        );
        return voxelizer;
    }

    @Override
    public String salt() {
        return "";
    }
}

package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;

// TODO: Javascript (a model representing a bounding box (tile, world, ...))
public class BBoxModel extends ModelImpl implements Shape2dConvertibleModel {

    private final WorldBBox2d bbox;

    // TODO: Javascirpt
    public BBoxModel(WorldBBox2d bbox) {
        this.bbox = bbox;
    }

    @Override
    public Shape2d toShape2d() {
        return new Polygon2d(
            LinearRing2d.fromPoints(
                new WorldCoords2d(bbox.minX(), bbox.minY()),
                new WorldCoords2d(bbox.minX(), bbox.maxY()),
                new WorldCoords2d(bbox.maxX(), bbox.maxY()),
                new WorldCoords2d(bbox.maxX(), bbox.minY())
            )
        );
    }

    @Override
    public String salt() {
        return bbox.toString();
    }
}

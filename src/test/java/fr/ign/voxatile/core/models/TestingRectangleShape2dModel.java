package fr.ign.voxatile.core.models;

import fr.ign.voxatile.core.utils.world2d.WorldBBox2d;
import fr.ign.voxatile.core.utils.world2d.WorldCoords2d;
import fr.ign.voxatile.core.voxelization.shape2d.LinearRing2d;
import fr.ign.voxatile.core.voxelization.shape2d.Polygon2d;
import fr.ign.voxatile.core.voxelization.shape2d.Shape2d;

/**
 * A model voxelizable in 2d consisting of a simple rectangle.
 */
public class TestingRectangleShape2dModel extends ModelImpl implements Shape2dConvertibleModel {
    private final WorldBBox2d bbox;

    /**
     * Creates a new model with given bbox.
     *
     * @param bbox rectangle of this model in world
     */
    public TestingRectangleShape2dModel(WorldBBox2d bbox) {
        super();
        this.bbox = bbox;
    }

    @Override
    public Shape2d toShape2d() {
        return new Polygon2d(
            LinearRing2d.fromPoints(
                bbox.min(),
                new WorldCoords2d(bbox.minX(), bbox.maxY()),
                bbox.max(),
                new WorldCoords2d(bbox.maxX(), bbox.minY())
            )
        );
    }

    @Override
    public String salt() {
        return "";
    }
}

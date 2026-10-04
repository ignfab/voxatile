package fr.ign.voxatile.core.voxelization.shape2d.voxelizer;

import fr.ign.voxatile.core.utils.iterator.Iterables;
import fr.ign.voxatile.core.utils.world2d.Positioned2d;
import fr.ign.voxatile.core.voxelization.shape2d.Polygon2d;
import fr.ign.voxatile.core.voxelization.shape2d.Shape2dConvertible;
import fr.ign.voxatile.core.voxelization.shape2d.iterator.Polygon2dIterator;

/**
 * A voxelizer for surfacic shapes.
 */
public class SurfaceVoxelizer2d implements Shape2dVoxelizer {
    /**
     * Voxelizes a polygon.
     *
     * @param polygon Polygon to voxelize
     * @return an iterable over voxelized positions.
     */
    public Iterable<Positioned2d> voxelize(Polygon2d polygon) {
        return () -> new Polygon2dIterator(polygon, true);
    }

    @Override
    public Iterable<Positioned2d> voxelize(Shape2dConvertible convertible) {
        return Iterables.flatMap(convertible.toShape2d().polygons(), this::voxelize);
    }

}

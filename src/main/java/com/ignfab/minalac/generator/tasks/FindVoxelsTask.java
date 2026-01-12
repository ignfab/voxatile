package com.ignfab.minalac.generator.tasks;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Point2d;

/**
 * A {@link TileTask} looking for lowest and/or highest voxels placed over the model and adding results as metadata.
 */
public class FindVoxelsTask extends ModelTask<Shape2dConvertibleModel> {
    private final Predicate<Placeable> filter;
    private final String lowest;
    private final String highest;
    private final String average;

    /**
     * Creates a new {@code FindVoxelsTask}.
     * @param selection the models to use
     * @param filter the filter to select which voxels to match
     * @param lowest metadata where to store z-coordinate of lowest voxel found
     * @param highest metadata where to store z-coordinate of highest voxel found
     * @param average metadata where to store average z-coordinate of voxels found
     */
    public FindVoxelsTask(ModelSelection selection, Predicate<Placeable> filter, String lowest, String highest, String average) {
        super(Shape2dConvertibleModel.class, selection);
        this.filter = filter;
        this.lowest = lowest;
        this.highest = highest;
        this.average = average;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        if (model.toShape2d() instanceof Point2d point && tile.limits().to2d().contains(point)) {
            WorldCoords2d c = point.coords();
            int minZ = tile.voxels().minVoxelZ(c.x(), c.y());
            int maxZ = tile.voxels().maxVoxelZ(c.x(), c.y());
            if (minZ > maxZ)
                return; // No voxel placed in that column

            if (lowest != null) {
                for (int z = minZ; z <= maxZ; z++) {
                    Placeable voxel = tile.voxels().getVoxel(c.x(), c.y(), z);
                    if (voxel != null && filter.test(voxel)) {
                        model.setMetadata(lowest, z);
                        break;
                    }
                }
            }

            if (highest != null) {
                for (int z = maxZ; z >= minZ; z--) {
                    Placeable voxel = tile.voxels().getVoxel(c.x(), c.y(), z);
                    if (voxel != null && filter.test(voxel)) {
                        model.setMetadata(highest, z);
                        break;
                    }
                }
            }

            if (average != null) {
                int total = 0;
                int number = 0;
                for (int z = maxZ; z >= minZ; z--) {
                    Placeable voxel = tile.voxels().getVoxel(c.x(), c.y(), z);
                    if (voxel != null && filter.test(voxel)) {
                        number++;
                        total += z;
                    }
                }
                if (number > 0)
                    model.setMetadata(average, (int) Math.round(total / (double) number));
            }
        }
    }
}

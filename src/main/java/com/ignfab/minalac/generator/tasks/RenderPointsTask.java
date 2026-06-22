package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape3dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape3d.voxelizer.Point3dVoxelizer;

/**
 * A task rendering points by placing placeables at them.
 */
public class RenderPointsTask extends ModelTask<Shape3dConvertibleModel> {
    private final Placeable placeable;
    private final Point3dVoxelizer voxelizer;

    /**
     * Creates a new {@code RenderPointsTask}.
     *
     * @param selection selection of models to render
     * @param placeable placeable placed at the points
     */
    public RenderPointsTask(
        ModelSelection selection,
        Placeable placeable
    ) {
        super(Shape3dConvertibleModel.class, selection);
        this.placeable = placeable;
        voxelizer = new Point3dVoxelizer();
    }

    @Override
    protected void run(Shape3dConvertibleModel model, GenerationTile tile) {
        for (Positioned3d point : voxelizer.voxelize(model))
            placeable.place(tile.voxels(), point.coords());
    }
}

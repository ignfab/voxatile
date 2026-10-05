package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.Shape3dConvertibleModel;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.world3d.Positioned3d;
import fr.ign.voxatile.core.voxelization.shape3d.voxelizer.Point3dVoxelizer;

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
        for (Positioned3d point : tile.limits().filterInside(voxelizer.voxelize(model)))
            placeable.place(tile.voxels(), point.coords());
    }
}

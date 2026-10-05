package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.Shape2dConvertibleModel;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.world2d.Positioned2d;
import fr.ign.voxatile.core.utils.world2d.WorldCoords2d;
import fr.ign.voxatile.core.voxelization.shape2d.voxelizer.Point2dVoxelizer;

/**
 * A task rendering points by placing placeables at them.
 */
public class RenderPoints2dTask extends ModelTask<Shape2dConvertibleModel> {
    private final Placeable placeable;
    private final ReadableHeightmapSpec heightmapSpec;
    private final Point2dVoxelizer voxelizer;

    /**
     * Creates a new {@code RenderPoints2dTask}.
     *
     * @param selection selection of models to render
     * @param placeable placeable placed at the points
     * @param heightmapSpec heightmap on which draw points
     */
    public RenderPoints2dTask(
        ModelSelection selection,
        Placeable placeable,
        ReadableHeightmapSpec heightmapSpec
    ) {
        super(Shape2dConvertibleModel.class, selection);
        this.placeable = placeable;
        this.heightmapSpec = heightmapSpec;
        voxelizer = new Point2dVoxelizer();
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmap(heightmapSpec);
        for (Positioned2d point : tile.limits().to2d().filterInside(voxelizer.voxelize(model))) {
            WorldCoords2d pos = point.coords();
            placeable.place(tile.voxels(), pos.x(), pos.y(), heightmap.get(pos));
        }
    }
}

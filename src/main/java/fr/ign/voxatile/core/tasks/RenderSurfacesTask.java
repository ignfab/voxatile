package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.Shape2dConvertibleModel;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.world2d.Positioned2d;
import fr.ign.voxatile.core.utils.world2d.WorldCoords2d;
import fr.ign.voxatile.core.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import fr.ign.voxatile.core.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link TileTask} placing things on a 2d surface shape at heighmap height.
 */
public class RenderSurfacesTask extends ModelTask<Shape2dConvertibleModel> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final Placeable placeable;

    private final Shape2dVoxelizer voxelizer = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code RenderSurfacesTask}.
     *
     * @param selection model selection containing the models to render
     * @param heightmapSpec heightmap on which features will be placed
     * @param placeable what to place on surface
     */
    public RenderSurfacesTask(ModelSelection selection, ReadableHeightmapSpec heightmapSpec, Placeable placeable) {
        super(Shape2dConvertibleModel.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.placeable = placeable;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmap(heightmapSpec);

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(model))) {
            WorldCoords2d c = voxel.coords();
            placeable.place(tile.voxels(), c.x(), c.y(), heightmap.get(c));
        }
    }
}

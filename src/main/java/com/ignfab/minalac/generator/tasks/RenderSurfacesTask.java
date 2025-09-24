package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link TileTask} placing things on vector models.
 * <p>
 * Placement is done on a heightmap, ignoring Z component of model geometries.
 * Placement can be done on borders (for all sorts of geometry) and/or inside (for polygon geometries) or everywhere.
 */
public class RenderSurfacesTask extends ModelTask<Shape2dConvertibleModel> {
    private final GetZ atZ;
    private final Placeable placeable;

    private final SurfaceVoxelizer2d voxelizer = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param atZ Getter for the altitude at which features will be placed
     * @param placeable What to place on surface
     */
    public RenderSurfacesTask(ModelSelection selection, GetZ atZ, Placeable placeable) {
        super(Shape2dConvertibleModel.class, selection);
        this.atZ = atZ;
        this.placeable = placeable;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) throws IgnorableException {
        for (Positioned2d voxel : tile.clip2d(voxelizer.voxelize(model))) {
            WorldCoords2d c = voxel.coords();
            placeable.place(tile.voxels(), c.x(), c.y(), atZ.get(tile, model, c));
        }
    }

    @FunctionalInterface
    public interface GetZ {
        int get(GenerationTile tile, Model model, WorldCoords2d coords) throws IgnorableException;
    }
}

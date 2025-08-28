package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
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
    private final ReadableHeightmapSpec heightmapSpec;
    private final Placeable placeable;

    private final SurfaceVoxelizer2d voxelizer = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmapSpec Heightmap of the ground (on which features will be placed)
     * @param placeable What to place on surface
     */
    public RenderSurfacesTask(ModelSelection selection, ReadableHeightmapSpec heightmapSpec, Placeable placeable) {
        super(Shape2dConvertibleModel.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.placeable = placeable;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);

        for (Positioned2d voxel : tile.clip2d(voxelizer.voxelize(model))) {
            WorldCoords2d c = voxel.coords();
            placeable.place(tile.voxels(), c.x(), c.y(), heightmap.get(c));
        }
    }
}

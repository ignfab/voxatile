package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ThinLinearVoxelizer2d;

/**
 * A {@link TileTask} placing things on vector models.
 * <p>
 * Placement is done on a heightmap, ignoring Z component of model geometries.
 * Placement can be done on borders (for all sorts of geometry) and/or inside (for polygon geometries) or everywhere.
 */
public class RenderVectorsTask extends ModelTask<Shape2dConvertibleModel> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final ThinLinearVoxelizer2d linearVoxelizer = new ThinLinearVoxelizer2d();;
    private final SurfaceVoxelizer2d surfaceVoxelizer = new SurfaceVoxelizer2d();

    // What to place inside and on borders of geometries
    private final Placeable inside;
    private final Placeable borders;

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param inside What to place inside geometries
     * @param borders What to place on geometries borders
     */
    public RenderVectorsTask(ModelSelection selection, ReadableHeightmapSpec heightmap, Placeable inside, Placeable borders) {
        super(Shape2dConvertibleModel.class, selection);
        heightmapSpec = heightmap;
        this.inside = inside;
        this.borders = borders;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);

        if (inside != Nothing.INSTANCE)
            for (Positioned2d voxel : tile.clip2d(surfaceVoxelizer.voxelize(model))) {
                WorldCoords2d c = voxel.coords();
                inside.place(tile.voxels(), c.x(), c.y(), heightmap.get(c));
            }

        if (borders != Nothing.INSTANCE)
            for (Positioned2d voxel : tile.clip2d(linearVoxelizer.voxelize(model))) {
                WorldCoords2d c = voxel.coords();
                borders.place(tile.voxels(), c.x(), c.y(), heightmap.get(c));
            }
    }

}

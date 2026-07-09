package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.PointVoxelizer2d;

/**
 * A task rendering points by placing placeables at them.
 */
public class RenderPoints2dTask extends ModelTask<Shape2dConvertibleModel> {
    private final Placeable placeable;
    private final ReadableHeightmapSpec heightmapSpec;
    private final PointVoxelizer2d voxelizer;

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
        voxelizer = new PointVoxelizer2d();
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmap(heightmapSpec);
        for (Positioned2d point : voxelizer.voxelize(model)) {
            WorldCoords2d pos = point.coords();
            placeable.place(tile.voxels(), pos.x(), pos.y(), heightmap.get(pos));
        }
    }
}

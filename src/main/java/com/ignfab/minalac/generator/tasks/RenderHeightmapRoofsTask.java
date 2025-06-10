package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A {@link TileTask} rendering a {@link ModelSelection} as roofs.
 *
 * The {@link ModelSelection} must represent buildings and each model must include
 * the <code>height</code>, <code>minimum-ground-altitude</code>, and
 * <code>ground-floor-altitude</code> metadata.
 */
public class RenderHeightmapRoofsTask extends ModelTask<Voxelizable2d> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final Placeable roof;

    /**
     * Creates a new {@code RenderHeightmapRoofsTask}.
     *
     * @param selection building models selection
     * @param heightmapSpec heightmap of the surface
     * @param roof {@code Placeable} for roofs
     */
    public RenderHeightmapRoofsTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        Placeable roof
    ) {
        super(Voxelizable2d.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.roof = roof;
    }

    @Override
    protected void run(Voxelizable2d model, GenerationTile tile) {
        Integer zMinLeveling = model.getMetadata("minimum-ground-altitude");
        Integer zMaxLeveling = model.getMetadata("ground-floor-altitude");
        if (zMinLeveling == null || zMaxLeveling == null)
            return;

        // TODO: Implement a post-processor for value rounding to rollback this change
        int height = (int) Math.round(
            /* Casting to Number is needed to avoid a cast exception in RenderBuildingsTask */
            ((Number) model.getMetadata("height")).doubleValue()
        );
        height = Math.max(0, height - (zMaxLeveling - zMinLeveling));

        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);
        for (Positioned2d voxel : model.voxelize2d(tile.limits().to2d())) {
            WorldCoords2d c = voxel.coords();
            for (int z = zMaxLeveling + height + 1; z <= heightmap.get(c); z++)
                roof.place(tile.voxels(), c.x(), c.y(), z);
        }
    }
}

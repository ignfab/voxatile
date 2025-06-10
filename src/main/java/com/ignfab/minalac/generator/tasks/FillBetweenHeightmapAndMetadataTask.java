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
 * A {@link TileTask} which, for each model in {@link ModelSelection},
 * fills with {@link Placeable} the gap between a heightmap and an altitude (given by a model metadata)
 * within the model's boundaries.
 */
public class FillBetweenHeightmapAndMetadataTask extends ModelTask<Voxelizable2d> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final String altitudeMetadata;
    private final Placeable placeAbove;
    private final Placeable placeBelow;

    /**
     * Creates a new {@code FillBetweenHeightmapAndMetadataTask}.
     *
     * @param selection {@code ModelSelection} to use
     * @param heightmapSpec {@code ReadableHeightmap} to use
     * @param altitudeMetadata name of the model metadata containing the altitude value
     * @param placeAbove {@code Placeable} used to render voxels above the altitude value
     * @param placeBelow {@code Placeable} used to render voxels below the altitude value
     */
    public FillBetweenHeightmapAndMetadataTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        String altitudeMetadata,
        Placeable placeAbove,
        Placeable placeBelow
    ) {
        super(Voxelizable2d.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.altitudeMetadata = altitudeMetadata;
        this.placeAbove = placeAbove;
        this.placeBelow = placeBelow;
    }

    @Override
    protected void run(Voxelizable2d model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);
        Integer altitude = model.getMetadata(altitudeMetadata);
        // TODO should we use a FailurePolicy ?
        if (altitude == null)
            return;

        for (Positioned2d voxel : model.voxelize2d(tile.limits().to2d())) {
            WorldCoords2d c = voxel.coords();

            int height = heightmap.get(c);
            Placeable place = height <= altitude ? placeBelow : placeAbove;
            int min = Math.max(Math.min(height, altitude), tile.limits().minZ());
            int max = Math.min(Math.max(height, altitude), tile.limits().maxZ());
            for (int z = min; z <= max; z++)
                place.place(tile.voxels(), c.x(), c.y(), z);
        }
    }
}

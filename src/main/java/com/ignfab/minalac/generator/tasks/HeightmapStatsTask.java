package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;

/**
 * A {@link TileTask} which computes heightmap statistics over a model surface and adds the results as a metadata.
 * The heightmap values must be expressed in voxel units in order to have metadata in voxel unit.
 */
public class HeightmapStatsTask extends ModelTask<Voxelizable2d> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final String minimum;
    private final String maximum;

    /**
     * Creates a new {@code HeightmapStatsTask}.
     *
     * @param selection {@link ModelSelection} to use for computing statistics
     * @param heightmapSpec {@link ReadableHeightmap} used to compute statistics
     * @param minimum metadata where to store computed minimum value
     * @param maximum metadata where to store computed maximum value
     */
    public HeightmapStatsTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        String minimum,
        String maximum
    ) {
        super(Voxelizable2d.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    protected void run(Voxelizable2d model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);

        boolean empty = true;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Positioned2d voxel : model.voxelize2d(tile.limits().to2d().intersection(heightmap.bbox()))) {
            empty = false;

            int value = heightmap.get(voxel.coords());
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        if (empty) return;

        if (minimum != null) model.setMetadata(minimum, min);
        if (maximum != null) model.setMetadata(maximum, max);
    }
}

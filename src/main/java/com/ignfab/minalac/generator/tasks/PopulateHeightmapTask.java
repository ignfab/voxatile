package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * A {@link TileTask} copying data from a {@link ModelSelection} to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class PopulateHeightmapTask extends ModelTask<FloatMatrixModel> {
    private final WritableHeightmapSpec heightmapSpec;

    /**
     * Creates a new {@code PopulateHeightmapTask}.
     *
     * @param selection the model selection containing the wanted models
     * @param heightmapSpec Spec of writable heightmap where heights will be written
     */
    public PopulateHeightmapTask(ModelSelection selection, WritableHeightmapSpec heightmapSpec) {
        super(FloatMatrixModel.class, selection);
        this.heightmapSpec = heightmapSpec;
    }

    @Override
    protected void run(FloatMatrixModel model, GenerationTile tile) {
        WritableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);
        WorldBBox2d intersection = tile.limits().to2d().intersection(heightmap.bbox());
        // Iterate over matrix and fill heightmap altitude
        for (Matrix2d.Value<Float> value : model) {
            WorldCoords2d c = value.coords();
            if (intersection.contains(c))
                heightmap.set(c, Math.round(value.value()));
        }
    }
}

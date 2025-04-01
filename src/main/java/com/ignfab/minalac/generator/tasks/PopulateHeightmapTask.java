package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * A {@link TileTask} copying data from a {@link ModelSelection} to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class PopulateHeightmapTask extends ModelTask<FloatMatrixModel> {
    private final Heightmap heightmap;

    /**
     * Creates a new {@code PopulateHeightmapTask}.
     *
     * @param selection the model selection containing the wanted models
     * @param heightmap Heightmap where heights will be written
     */
    public PopulateHeightmapTask(ModelSelection selection, Heightmap heightmap) {
        super(FloatMatrixModel.class, selection);
        this.heightmap = heightmap;
    }

    @Override
    protected void run(FloatMatrixModel model, WorldBBox3d bbox) {
        WorldBBox2d intersection = bbox.to2d().intersection(heightmap.bbox());
        // Iterate over matrix and fill heightmap altitude
        for (Matrix2d.Value<Float> value : model) {
            WorldCoords2d c = value.coords();
            if (intersection.contains(c))
                heightmap.set(c, Math.round(value.value()));
        }
    }
}

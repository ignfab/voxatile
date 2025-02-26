package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * Heightmap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class HeightmapRenderer extends ModelRenderer<FloatMatrixModel> {
    private final Heightmap heightmap;

    /**
     * Creates a new HeightmapRenderer.
     *
     * @param selection the model selection containing the wanted models to render (only {@code FloatMatrixModel} will be)
     * @param heightmap Heightmap where heights will be written
     */
    public HeightmapRenderer(ModelSelection selection, Heightmap heightmap) {
        super(null, FloatMatrixModel.class, selection);
        this.heightmap = heightmap;
    }

    @Override
    protected void render(Seed seed, FloatMatrixModel model, WorldBBox3d bbox) {
        // Iterate over matrix and fill heightmap altitude
        for (Matrix2d.Value<Float> value : model) {
            WorldCoords2d c = value.coords();
            if (heightmap.bbox().contains(c)) {
                heightmap.set(c, Math.round(value.value()));
            }
        }
    }
}

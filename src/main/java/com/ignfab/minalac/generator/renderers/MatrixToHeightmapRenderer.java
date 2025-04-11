package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

// TODO: Rename this class as it is not a renderer per se. Should be be an "operation" or "task".
/**
 * Heightmap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class MatrixToHeightmapRenderer extends ModelRenderer<FloatMatrixModel> {
    private final UnboundHeightmap heightmap;

    /**
     * Creates a new {@code MatrixToHeightmapRenderer}.
     *
     * @param selection the model selection containing the wanted models to render
     * @param heightmap Heightmap where heights will be written
     */
    public MatrixToHeightmapRenderer(ModelSelection selection, UnboundHeightmap heightmap) {
        super(FloatMatrixModel.class, selection);
        this.heightmap = heightmap;
    }

    @Override
    protected void render(FloatMatrixModel model, GenerationTile tile) {
        Heightmap heightmap = this.heightmap.bind(tile);
        WorldBBox2d intersection = tile.limits().to2d().intersection(heightmap.bbox());
        // Iterate over matrix and fill heightmap altitude
        for (Matrix2d.Value<Float> value : model) {
            WorldCoords2d c = value.coords();
            if (intersection.contains(c))
                heightmap.set(c, Math.round(value.value()));
        }
    }
}

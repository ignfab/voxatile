package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * Heightmap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class HeightmapRenderer extends ModelRenderer {
    private Heightmap heightmap;

    /**
     * Creates a new HeightmapRenderer.
     *
     * @param selection the model selection containing the wanted models to render (only {@code FloatMatrixModel} will be)
     * @param heightmap Heightmap where heights will be written
     */
    public HeightmapRenderer(ModelSelection selection, Heightmap heightmap) {
        super(selection);
        this.heightmap = heightmap;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof FloatMatrixModel matrix)) {
            // TODO: Better warning about not possible to render a non float matrix model
            System.out.println("Ignoring non float matrix model.");
            return;
        }

        // Iterate over matrix and fill heightmap altitude
        for (Matrix2d.Value<Float> value : matrix) {
            WorldCoords2d c = value.coords();
            if (heightmap.bbox().contains(c)) {
                heightmap.set(c, (int) Math.round(value.value()));
            }
        }
    }
}

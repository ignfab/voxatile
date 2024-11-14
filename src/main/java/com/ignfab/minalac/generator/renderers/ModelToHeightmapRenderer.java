package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Heightmap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class ModelToHeightmapRenderer extends ModelRenderer {
    private Heightmap heightmap;

    /**
     * Creates a new HeightmapRenderer.
     *
     * @param selection the model selection containing the wanted models to render (only {@code FloatMatrixModel} will be)
     * @param heightmap Heightmap where heights will be written
     */
    public ModelToHeightmapRenderer(ModelFilter selection, Heightmap heightmap) {
        super(selection);
        this.heightmap = heightmap;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof Voxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non float matrix model
            System.out.println("Ignoring non float matrix model.");
            return;
        }

        // Iterate over matrix and fill heightmap altitude
        for (Positioned3d p : voxelizable.voxelize3d(bbox)) {
            WorldCoords2d c = p.coords().to2d();
            int z = p.coords().z();
            if (heightmap.bbox().contains(c) && heightmap.get(c) < z)
                heightmap.set(c, z);
        }
    }
}

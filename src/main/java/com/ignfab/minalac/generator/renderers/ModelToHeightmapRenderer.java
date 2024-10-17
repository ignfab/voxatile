package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Model to heightmap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the highest value is kept.
 */
public class ModelToHeightmapRenderer extends ModelRenderer<Voxelizable3d> {
    private final Heightmap heightmap;

    /**
     * Creates a new ModelToHeightmapRenderer.
     *
     * @param selection the model selection containing the wanted models to render (only {@code Voxelizable3d} will be)
     * @param heightmap Heightmap where heights will be written
     */
    public ModelToHeightmapRenderer(ModelSelection selection, Heightmap heightmap) {
        super(Voxelizable3d.class, selection);
        this.heightmap = heightmap;
    }

    @Override
    protected void render(Voxelizable3d model, WorldBBox3d bbox) {
        // Iterate over model and fill heightmap altitude
        for (Positioned3d p : model.voxelize3d(bbox)) {
            WorldCoords2d c = p.coords().to2d();
            int z = p.coords().z();
            if (heightmap.bbox().contains(c) && heightmap.get(c) < z)
                heightmap.set(c, z);
        }
    }
}

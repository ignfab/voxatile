package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

public class HeightmapMaskRenderer extends ModelRenderer {
    private final Heightmap heightmap;
    private final int value;

    public HeightmapMaskRenderer(ModelFilter selection, Heightmap heightmap, int value) {
        super(selection);
        this.heightmap = heightmap;
        this.value = value;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof Voxelizable2d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
            return;
        }
        Voxelizer2d voxelizer = voxelizable.voxelize2d(bbox.to2d());

        for (Positioned2d voxel : voxelizer)
            heightmap.set(voxel.coords(), value);
    }
}

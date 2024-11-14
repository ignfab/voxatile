package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

import java.util.Map;

public class ClassifyFromHeightmapRenderer extends ModelRenderer {
    private final Heightmap heightmap;
    private final Map<Integer, String> classes;

    public ClassifyFromHeightmapRenderer(ModelFilter selection, Heightmap heightmap, Map<Integer, String> classes) {
        super(selection);
        this.heightmap = heightmap;
        this.classes = classes;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof Voxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non shapesvoxelizable model. Type: " + model.getClass());
            return;
        }
        Voxelizer3d voxelizer = voxelizable.voxelize3d(bbox);

        for (Positioned3d voxel : voxelizer) {
            String classification = classes.get(heightmap.get(voxel.coords().to2d()));
            if (classification != null)
                model.setMetadata("classification", classification);
        }
    }
}

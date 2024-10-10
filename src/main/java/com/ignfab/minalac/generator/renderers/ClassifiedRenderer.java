package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelType;

import java.util.Map;

public class ClassifiedRenderer extends ModelRenderer {
    private final Map<String, VoxelType> classes;
    private final VoxelType defaultVoxel;

    public ClassifiedRenderer(ModelSelection selection, Map<String, VoxelType> classes, VoxelType defaultVoxel) {
        super(selection);
        this.classes = classes;
        this.defaultVoxel = defaultVoxel;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof Voxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
            return;
        }

        VoxelType voxel = classes.getOrDefault((String) model.getMetadata("classification"), defaultVoxel);
        for (Positioned3d v : voxelizable.voxelize3d(bbox))
            voxel.place(v.coords().x(), v.coords().y(), v.coords().z());
    }
}

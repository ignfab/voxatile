package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.ReadableHeightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelType;

import java.util.Map;

public class SurfaceClassifiedRenderer extends ModelRenderer {
    private final ReadableHeightmap heightmap;
    private final Map<String, VoxelType> classes;
    private final VoxelType defaultVoxel;

    public SurfaceClassifiedRenderer(ModelSelection selection, ReadableHeightmap heightmap, Map<String, VoxelType> classes, VoxelType defaultVoxel) {
        super(selection);
        this.heightmap = heightmap;
        this.classes = classes;
        this.defaultVoxel = defaultVoxel;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof Voxelizable2d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
            return;
        }

        int h = model.hasMetadata("height") ? (int) Math.round((double) model.getMetadata("height")) : 1;
        VoxelType voxel = classes.getOrDefault((String) model.getMetadata("classification"), defaultVoxel);
        for (Positioned2d v : voxelizable.voxelize2d(heightmap.bbox().intersection(bbox.to2d()))) {
            int z = heightmap.get(v.coords()) + 1;
            for (int i = 0; i < h; i++)
                voxel.place(v.coords().x(), v.coords().y(), z + i);
        }
    }
}

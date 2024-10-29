package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.models.BuildingSurfaceType;
import com.ignfab.minalac.generator.models.BuildingVoxelizable;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.BuildingVoxelizer;
import com.ignfab.minalac.generator.world.VoxelType;

import java.util.Map;

public class Building3dRenderer extends ModelRenderer {
    private final VoxelType ground;
    private final VoxelType wall;
    private final Map<String, VoxelType> roofs;
    private final VoxelType defaultRoof;

    public Building3dRenderer(ModelSelection selection, VoxelType ground, VoxelType wall, Map<String, VoxelType> roofs, VoxelType defaultRoof) {
        super(selection);
        this.ground = ground;
        this.wall = wall;
        this.roofs = roofs;
        this.defaultRoof = defaultRoof;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof BuildingVoxelizable voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non buildingvoxelizable model. Type: " + model.getClass());
            return;
        }
        BuildingVoxelizer voxelizer = voxelizable.voxelize3d(bbox);

        VoxelType roof = roofs.getOrDefault((String) model.getMetadata(/*"classification"*/"OGRLoader.usage_1"), defaultRoof);
        for (Positioned3d p : voxelizer.surfaces(BuildingSurfaceType.GROUND))
            ground.place(p.coords().x(), p.coords().y(), p.coords().z());
        for (Positioned3d p : voxelizer.surfaces(BuildingSurfaceType.WALL))
            wall.place(p.coords().x(), p.coords().y(), p.coords().z());
        for (Positioned3d p : voxelizer.surfaces(BuildingSurfaceType.ROOF))
            roof.place(p.coords().x(), p.coords().y(), p.coords().z());
    }
}

package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.BuildingSurfaceType;
import com.ignfab.minalac.generator.models.BuildingVoxelizable;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.BuildingVoxelizer;

public class RenderBuildings3dTask extends ModelTask<BuildingVoxelizable> {

    private final Placeable placeable;
    private final BuildingSurfaceType type;

    public RenderBuildings3dTask(ModelSelection selection, Placeable placeable, BuildingSurfaceType type) {
        super(BuildingVoxelizable.class, selection);
        this.placeable = placeable;
        this.type = type;
    }

    @Override
    protected void run(BuildingVoxelizable model, GenerationTile tile) {
        BuildingVoxelizer voxelizer = model.voxelize3d(tile.limits());

        for (Positioned3d p : voxelizer.surfaces(type))
            placeable.place(tile.voxels(), p.coords());
    }
}

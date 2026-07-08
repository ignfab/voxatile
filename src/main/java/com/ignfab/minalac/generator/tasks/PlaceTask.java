package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * A task placing a placeable according to a given voxelizer.
 */
public class PlaceTask extends ModelTask<Model> {
    private final Voxelizer3d voxelizer;
    private final Placeable placeable;

    /**
     * Creates a new {@code PlaceTask}.
     *
     * @param selection Selection of models to voxelize
     * @param voxelizer Voxelizer to use
     * @param placeable What to place on each voxel
     */
    public PlaceTask(ModelSelection selection, Voxelizer3d voxelizer, Placeable placeable) {
        super(Model.class, selection);
        this.voxelizer = voxelizer;
        this.placeable = placeable;
    }

    @Override
    protected void run(Model model, GenerationTile tile) {
        for (Positioned3d pos : tile.limits().filterInside(voxelizer.voxelize(model)))
            placeable.place(tile.voxels(), pos.coords());
    }
}

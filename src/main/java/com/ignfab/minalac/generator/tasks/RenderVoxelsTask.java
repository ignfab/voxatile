package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;

/**
 * A {@link TileTask} placing voxels on 3d models.
 * <p>
 * Placement is done at every position of the voxelized model.
 */
public class RenderVoxelsTask extends ModelTask<Voxelizable3d> {
    private final Placeable place;

    /**
     * Creates a new {@code RenderVoxelsTask}.
     * @param selection the models to render
     * @param place the placeable to use
     */
    public RenderVoxelsTask(ModelSelection selection, Placeable place) {
        super(Voxelizable3d.class, selection);
        this.place = place;
    }

    @Override
    protected void run(Voxelizable3d model, GenerationTile tile) {
        for (Positioned3d v : model.voxelize3d(tile.limits()))
            place.place(tile.voxels(), v.coords());
    }
}

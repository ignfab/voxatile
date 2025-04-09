package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A {@link TileTask} running on a {@link ModelSelection}.
 *
 * @param <M> Model type for this task.
 */
public abstract class ModelTask<M> implements TileTask {
    private final Class<M> cls;
    private final ModelSelection selection;

    protected ModelTask(Class<M> cls, ModelSelection selection) {
        this.cls = cls;
        this.selection = selection;
    }

    @Override
    public void run(VoxelTile tile) {
        for (Model model : selection)
            if (cls.isInstance(model))
                run(cls.cast(model), tile);
    }

    /**
     * Runs task for a given model.
     *
     * @param model concerned model
     * @param tile tile to render into
     */
    protected abstract void run(M model, VoxelTile tile);
}

package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

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
    public void run(WorldBBox3d bbox) {
        for (Model model : selection)
            if (cls.isInstance(model))
                run(cls.cast(model), bbox);
    }

    /**
     * Runs task for a given model.
     *
     * @param model concerned model
     * @param bbox generation tile limits
     */
    protected abstract void run(M model, WorldBBox3d bbox);
}

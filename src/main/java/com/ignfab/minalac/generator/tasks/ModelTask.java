package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;

/**
 * A {@link TileTask} running on a {@link ModelSelection}.
 *
 * @param <M> Model type for this task.
 */
public abstract class ModelTask<M extends Model> implements TileTask {
    private final Class<M> cls;
    private final ModelSelection selection;

    protected ModelTask(Class<M> cls, ModelSelection selection) {
        this.cls = cls;
        this.selection = selection;
    }

    @Override
    public void run(GenerationTile tile) {
        for (Model model : selection.forTile(tile)) {
            if (cls.isInstance(model)) {
                try {
                    run(cls.cast(model), tile);
                } catch (IgnorableException ignored) {}
            }
        }
    }

    /**
     * Runs task for a given model.
     *
     * @param model concerned model
     * @param tile tile to render into
     */
    // TODO: Tile may be passed using `GenerationTile.current()`
    protected abstract void run(M model, GenerationTile tile) throws IgnorableException;
}

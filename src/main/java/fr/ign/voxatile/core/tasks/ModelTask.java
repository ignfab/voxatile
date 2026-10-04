package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.exceptions.IgnorableException;
import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.ModelSelection;

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

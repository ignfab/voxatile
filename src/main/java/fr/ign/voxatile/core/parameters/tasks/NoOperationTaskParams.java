package fr.ign.voxatile.core.parameters.tasks;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.tasks.NoOperationTask;
import fr.ign.voxatile.core.utils.execution.Task;

/**
 * Parameters for a {@link NoOperationTask}.
 */
public class NoOperationTaskParams extends TaskParams {
    @Override
    public Task create(Generation generation) {
         return NoOperationTask.INSTANCE;
    }
}

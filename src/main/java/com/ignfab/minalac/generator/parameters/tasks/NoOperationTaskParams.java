package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a {@link NoOperationTask}.
 */
public class NoOperationTaskParams extends TaskParams {
    @Override
    public Task create(Generation generation) {
         return NoOperationTask.INSTANCE;
    }
}

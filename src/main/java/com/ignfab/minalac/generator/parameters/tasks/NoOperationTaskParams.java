package com.ignfab.minalac.generator.parameters.tasks;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a {@link NoOperationTask}.
 */
public class NoOperationTaskParams extends TileTaskParams {

    @Override
    public TileTask create(Generation generation) {
         return NoOperationTask.INSTANCE;
    }
}

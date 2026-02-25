package com.ignfab.minalac.generator.parameters.tasks.generic;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a {@link NoOperationTask}.
 *
 * @param <T> task execution context type (same as in {@link Task<T>}).
 */
public class NoOperationTaskParams<T> extends TaskParams<T> {
    @Override
    public Task<T> create(Generation generation) {
         return NoOperationTask.instance();
    }
}

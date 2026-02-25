package com.ignfab.minalac.generator.parameters.tasks.generic;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * An invalid TaskParams class for testing purposes.
 */
public class TestingInvalidTaskParams extends TaskParams<Object> {
    @Override
    public void validate() {
        throw new IllegalArgumentException();
    }

    @Override
    public Task<Object> create(Generation generation) {
        return null;
    }
}

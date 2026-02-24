package com.ignfab.minalac.generator.utils.execution;

public interface Task<T> {
    /**
     * Runs task.
     *
     * @param context for task
     */
    void run(T context);
}

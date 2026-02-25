package com.ignfab.minalac.generator.utils.execution;

/**
 * A runnable task.
 *
 * @param <T> Type of run context (type for {@link #run} argument)
 */
public interface Task<T> {
    /**
     * Runs task.
     *
     * @param context for task
     */
    void run(T context);
}

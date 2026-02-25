package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * A task doing absolutely nothing (because nothing is important).
 * <p>
 * Very convenient to wait for other tasks and use same abstract name in subsequent {@code after} fields.
 * <p>
 * This class is a singleton, use {@link #instance()} instead of constructor.
 */
public final class NoOperationTask implements Task<Object> {
    private static NoOperationTask instance = new NoOperationTask();

    /**
     * Returns a {@code NoOperationTask} of the right subtype.
     *
     * @param <T> tasks execution context type (same as in {@link Task<T>})
     *
     * @return {@code NoOperationTask} instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Task<T> instance() {
        return (Task<T>) instance;
    }

    private NoOperationTask() {}

    @Override
    public void run(Object context) {}
}

package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * A task doing absolutely nothing (because nothing is important).
 * <p>
 * Very convenient to wait for other tasks and use same abstract name in subsequent {@code after} fields.
 */
public class NoOperationTask implements Task<Object> {
    private static NoOperationTask INSTANCE = new NoOperationTask();

    /**
     * This class is a singleton, use this instead of constructor.
     */
    @SuppressWarnings("unchecked")
    public static <T> Task<T> instance() {
        return (Task<T>) INSTANCE;
    }

    private NoOperationTask() {
    }

    @Override
    public void run(Object context) {
    }
}

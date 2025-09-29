package com.ignfab.minalac.generator.tasks;

/**
 * A task doing absolutely nothing (because nothing is important).
 * <p>
 * Very convenient to wait for other tasks and use same abstract name in subsequent {@code after} fields.
 */
public final class NoOperationTask implements Task {

    private static final NoOperationTask INSTANCE = new NoOperationTask();

    /**
     * @return {@code NoOperationTask} instance.
     * <p>
     * This class is a singleton, use this instead of constructor.
     */
    public static NoOperationTask instance() {
        return INSTANCE;
    }

    private NoOperationTask() {}

    @Override
    public void run() {}
}

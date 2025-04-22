package com.ignfab.minalac.generator.utils.execution;

/**
 * Exception thrown when an exception occurs in a {@link ScheduledTask}.
 */
public class TaskFailedException extends Exception {
    private final ScheduledTask task;

    /**
     * Creates a new exception.
     *
     * @param task the failed task
     * @param cause the exception causing the failure
     */
    public TaskFailedException(ScheduledTask task, Throwable cause) {
        super("Task failed: " + task.id(), cause);
        this.task = task;
    }

    /**
     * Returns the failed task.
     *
     * @return the failed task
     */
    public ScheduledTask getTask() {
        return task;
    }
}

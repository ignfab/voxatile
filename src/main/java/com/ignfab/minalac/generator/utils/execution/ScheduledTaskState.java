package com.ignfab.minalac.generator.utils.execution;

/**
 * The state of a {@link ScheduledTask}.
 */
public enum ScheduledTaskState {
    /**
     * The task is waiting for other tasks to complete.
     * It will be launched once all conditions are fulfilled.
     */
    WAITING,
    /**
     * All conditions have been fulfilled, the task is being launched.
     * It will be running once the execution service has a thread ready.
     */
    LAUNCHING,
    /**
     * The task is running.
     * It will be finished once the task's runnable returns.
     * If an error occurs, it will stop everything and the state won't be updated.
     */
    RUNNING,
    /**
     * The task has finished.
     * It ran without error, and will be removed from the scheduler.
     */
    FINISHED
}

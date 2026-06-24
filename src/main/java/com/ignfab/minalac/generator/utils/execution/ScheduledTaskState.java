package com.ignfab.minalac.generator.utils.execution;

/**
 * The state of a {@link ScheduledTask}.
 */
public enum ScheduledTaskState {
    /**
     * The task is waiting for other tasks to complete.
     * It will be launched once all of its dependencies are finished.
     */
    WAITING,
    /**
     * All dependencies have been finished, the task is being launched.
     * It will be running once the execution service has a thread ready.
     */
    LAUNCHING,
    /**
     * The task is running.
     * It will be finished once the task's runnable returns.
     * If this task is interrupted following {@link ScheduledTask#cancel()} the state won't be updated.
     */
    RUNNING,
    /**
     * The task has finished.
     */
    FINISHED,

    /**
     * The task execution failed.
     */
    FAILED;

    public String toString() {
        return switch (this) {
            case WAITING   -> "Waiting";
            case LAUNCHING -> "Launching";
            case RUNNING   -> "Running";
            case FINISHED  -> "Finished";
            case FAILED    -> "Failed";
        };
    }
}

package com.ignfab.minalac.generator.utils.execution;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * A task registered in the {@link Scheduler}.
 */
public class ScheduledTask {
    private final String id;
    private final Runnable task;
    private final Set<ScheduledTask> dependencies;
    private ScheduledTaskState state;
    private TaskFailedException error;

    /**
     * Creates a new task with the given characteristics.
     *
     * @param id the ID of the task
     * @param task the runnable to execute
     */
    public ScheduledTask(String id, Runnable task) {
        this.id = id;
        this.task = task;
        this.dependencies = new HashSet<>();
        reset();
    }

    /**
     * Returns the ID of the task.
     *
     * @return the ID of the task
     */
    public String getId() {
        return id;
    }

    public void reset() {
        state = ScheduledTaskState.WAITING;
        error = null;
    }

    public boolean tryExecute(ExecutorService executor, SchedulerLock lock) {
        // Check if task can run
        if (state != ScheduledTaskState.WAITING)
            return false;
        for (ScheduledTask task : dependencies)
            if (task.getState() != ScheduledTaskState.FINISHED)
                return false;

        // It can, let's go!
        state = ScheduledTaskState.LAUNCHING;
        executor.execute(() -> {
            try {
                Thread.currentThread().setName(id);
                System.out.printf("[%s] %s%n", Thread.currentThread().getName(), "Starting " + id);
                state = ScheduledTaskState.RUNNING;
                task.run();
                state = ScheduledTaskState.FINISHED;
                System.out.printf("[%s] %s%n", Thread.currentThread().getName(), id + " finished");
                lock.notifyDone();

            } catch (RuntimeException e) {
                // If an error occurs, we take note of the task failure and wake up the main thread
                // The synchronized block is mandatory to take ownership of the lock
                System.out.printf("[%s] %s%n", Thread.currentThread().getName(), "Error in task " + id);

                synchronized (lock) {
                    state = ScheduledTaskState.ERROR;
                    error = new TaskFailedException(this, e);
                    lock.notifyDone();
                }
            }
        });
        return true;
    }

    /**
     * Adds a task that needs to be completed before this task can be run.
     *
     * @param dependency the dependency task
     */
    protected void addDependency(ScheduledTask dependency) {
        this.dependencies.add(dependency);
    }

    /**
     * Returns the state of this task.
     *
     * @return the state of this task
     */
    public ScheduledTaskState getState() {
        return state;
    }

    public TaskFailedException getError() {
        return error;
    }

}

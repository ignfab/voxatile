package com.ignfab.minalac.generator.utils.execution;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * A task registered in the {@link Scheduler}.
 *
 * @param <T> execution context type
 */
public class ScheduledTask<T> {
    private final String id;
    private final Consumer<T> task;
    private final Set<ScheduledTask<T>> dependencies;
    private ScheduledTaskState state;
    private TaskFailedException error;
    private Future<?> future;

    /**
     * Creates a new task with the given characteristics.
     *
     * @param id the ID of the task
     * @param task the runnable to execute
     */
    public ScheduledTask(String id, Consumer<T> task) {
        this.id = id;
        this.task = task;
        this.dependencies = new HashSet<>();
        reset();
    }

    /**
     * Resets this task so it can be re-executed.
     */
    public void reset() {
        state = ScheduledTaskState.WAITING;
        error = null;
        future = null;
    }

    /**
     * Tries to submit this task for execution.
     * The lock is used as a way to communicate to a potential main thread the end or failure of this task.
     *
     * @param executor the service where tasks are submitted for execution
     * @param lock the lock used for communication
     * @param context execution context for this task
     * @return true if the task was submitted for execution.
     */
    public boolean tryExecute(T context, ExecutorService executor, Object lock) {
        // Check if task can run
        if (state != ScheduledTaskState.WAITING)
            return false;
        for (ScheduledTask<T> task : dependencies)
            if (task.state() != ScheduledTaskState.FINISHED)
                return false;

        // It can, let's go!
        state = ScheduledTaskState.LAUNCHING;
        future = executor.submit(() -> {
            try {
                Thread.currentThread().setName(id);
                System.out.printf("Starting task %s%n", id);
                state = ScheduledTaskState.RUNNING;
                task.accept(context);
                state = ScheduledTaskState.FINISHED;
                System.out.printf("Task %s finished%n", id);
            } catch (RuntimeException e) {
                // If an error occurs, we take note of the task failure
                System.out.printf("Error in task %s%n", id);
                error = new TaskFailedException(this, e);
                state = ScheduledTaskState.FAILED;
            } finally {
                // On both cases, we wake up the main thread
                // The synchronized block is mandatory to take ownership of the lock
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });
        return true;
    }

    /**
     * Interrupts this task if it is running.
     */
    public void cancel() {
        if (future != null)
            future.cancel(true);
    }

    /**
     * Adds a task that needs to be completed before this task can be run.
     *
     * @param dependency the dependency task
     */
    protected void addDependency(ScheduledTask<T> dependency) {
        this.dependencies.add(dependency);
    }

    /**
     * {@return the ID of the task}
     */
    public String id() {
        return id;
    }

    /**
     * {@return the state of this task}
     */
    public ScheduledTaskState state() {
        return state;
    }

    /**
     * If an error occurred during execution of this task, returns the associated {@code TaskFailedException}.
     *
     * @return the associated {@code TaskFailedException}, {@code null} otherwise
     */
    public TaskFailedException error() {
        return error;
    }

}

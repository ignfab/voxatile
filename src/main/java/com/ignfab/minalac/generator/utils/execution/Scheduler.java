package com.ignfab.minalac.generator.utils.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A scheduler is a service managing execution of tasks.
 * It can perform parallel operations and ensure execution order between some tasks.
 */
public class Scheduler {
    // An executor using a thread pool to run tasks
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, ScheduledTask> tasks = new HashMap<>();
    // Object used for synchronized blocks, like a mutex.
    // .wait() / .notify() operations are performed on this object
    private SchedulerLock lock = new SchedulerLock();

    /**
     * Schedules the task to be executed when all dependencies are finished.
     * If the task has no dependency, it will be executed at {@link #runThenReset(long, TimeUnit)}.
     *
     * @param id the ID of the task
     * @param task the task to be scheduled
     */
    public void schedule(String id, Runnable task) {
        schedule(new ScheduledTask(id, task));
    }

    /**
     * Schedules the task to be executed when all dependencies are finished.
     * If the task has no condition, it will be executed at {@link #runThenReset(long, TimeUnit)}.
     *
     * @param task the task to be scheduled
     */
    public void schedule(ScheduledTask task) {
        if (tasks.containsKey(task.getId()))
            throw new IllegalArgumentException("Task %s already scheduled".formatted(task.getId()));
        tasks.put(task.getId(), task);
    }

    /**
     * Makes {@code idTask} dependent on the completion of the specified {@code idDependency} task.
     *
     * @param idTask the id of the task
     * @param idDependency the id the dependency
     */
    public void addDependency(String idTask, String idDependency) {
        getTask(idTask).addDependency(getTask(idDependency));
    }

    /**
     * Starts this scheduler and waits for the completion of all tasks.
     * Once all tasks are completed, resets this scheduler so the same set of tasks can be re-executed, this method can hence be used multiple times.
     * When waiting for task completion, this thread is paused until all tasks are over, or a single task fails.
     *
     * @param timeout the maximum amount of time before timing out
     * @param unit the unit of time for the timeout
     * @throws InterruptedException if the thread is interrupted
     * @throws TaskFailedException if a task fails
     */
    public void run(long timeout, TimeUnit unit) throws InterruptedException, TaskFailedException {
        tasks.values().forEach(ScheduledTask::reset);

        while (true) {
            boolean started = false;
            boolean waiting = false;
            boolean remaining = false;
            for (ScheduledTask task : tasks.values()) {
                switch(task.getState()) {
                    case FINISHED:
                        break;
                    case ERROR:
                        throw task.getError();
                    case WAITING:
                        remaining = true;
                        if (task.tryExecute(executor, lock))
                            started = true;
                        else
                            waiting = true;
                        break;
                    case LAUNCHING :
                    case RUNNING:
                        started = true;
                        remaining = true;
                }
            }

            if (waiting && !started)
                throw new  IllegalStateException("Deadlocked!");

            if (!remaining)
                break;

            if (!lock.waitDone(timeout))
                return;
        }
    }

    /**
     * Stops the underlying executor service.
     *
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        executor.shutdown();
    }

    private ScheduledTask getTask(String id) {
        if (!tasks.containsKey(id))
            throw new IllegalArgumentException("Task %s doesn't exist".formatted(id));
        return tasks.get(id);
    }
}

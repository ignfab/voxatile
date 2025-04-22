package com.ignfab.minalac.generator.utils.execution;

import java.util.Collection;
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
    // Stores the exception that occurred, if any.
    // Needed to pass the exception between threads
    private TaskFailedException error = null;
    // Object used for synchronized blocks, like a mutex.
    // .wait() / .notify() operations are performed on this object
    private final Object lock = new Object();

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

    private void tryExecute(ScheduledTask task) {
        synchronized (task) {
            // Only a waiting task that has all of its dependencies finished can be executed
            if (task.getState() != ScheduledTaskState.WAITING || !areTasksDone(task.getDependencies())) return;
            task.setState(ScheduledTaskState.LAUNCHING);
            // The .execute() method asks for execution but real execution can be delayed if no thread is currently available
            executor.execute(() -> {
                try {
                    Thread.currentThread().setName(task.getId());
                    task.setState(ScheduledTaskState.RUNNING);
                    task.run();
                    task.setState(ScheduledTaskState.FINISHED);
                    for (ScheduledTask dependent : task.getDependents())
                        tryExecute(dependent);
                    // If this was the last task, we wake up the main thread
                    // The synchronized block is mandatory to take ownership of the lock
                    synchronized (lock) {
                        if (areTasksDone(tasks.values()))
                            lock.notifyAll();
                    }
                } catch (RuntimeException e) {
                    // If an error occurs, we take note of the task failure and wake up the main thread
                    // The synchronized block is mandatory to take ownership of the lock
                    synchronized (lock) {
                        error = new TaskFailedException(task, e);
                        lock.notifyAll();
                    }
                }
            });
        }
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

    // Checking the state of tasks should be thread-safe, however
    // since FINISHED is the final state it is not strictly necessary to make this method synchronized.
    private static boolean areTasksDone(Collection<ScheduledTask> tasks) {
        for (ScheduledTask task : tasks)
            if (task.getState() != ScheduledTaskState.FINISHED)
                return false;
        return true;
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
    public void runThenReset(long timeout, TimeUnit unit) throws InterruptedException, TaskFailedException {
        // Reset the exception in the case there was any task failure in the precedent execution of this method
        error = null;
        tasks.values().forEach(task -> task.setState(ScheduledTaskState.WAITING));

        tasks.values().forEach(this::tryExecute);

        // Pause this thread by sleeping until being waked up by the end / failure of tasks
        // The synchronized block is mandatory to take ownership of the lock
        synchronized (lock) {
            // TODO Spurious wakeup guard
            lock.wait(unit.toMillis(timeout));
        }

        // Propagates the task failure, if any
        if (error != null)
            throw error;
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

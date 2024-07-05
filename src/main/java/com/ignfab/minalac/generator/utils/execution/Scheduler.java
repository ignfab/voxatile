package com.ignfab.minalac.generator.utils.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    // Synchronized collections are thread-safe equivalent of regular collections
    // This is important to ensure no problem occurs if two required tasks finishes at the same time
    private final List<ScheduledTask> tasks = Collections.synchronizedList(new ArrayList<>());

    // Stores the exception that occurred, if any.
    // Needed to pass the exception between threads
    private TaskFailedException error = null;
    // Object used for synchronized blocks, like a mutex.
    // .wait() / .notify() operations are performed on this object
    private final Object lock = new Object();

    /**
     * Schedules the task to be executed when all conditions are fulfilled.
     * If the task has no condition, it will be executed at {@link #start()}.
     *
     * @param id the ID of the task
     * @param task the task to be scheduled
     * @param conditions the conditions before the task may run
     */
    public void schedule(String id, Runnable task, Collection<String> conditions) {
        schedule(new ScheduledTask(id, task, conditions));
    }

    /**
     * Schedules the task to be executed when all conditions are fulfilled.
     * If the task has no condition, it will be executed at {@link #start()}.
     *
     * @param id the ID of the task
     * @param task the task to be scheduled
     * @param conditions the conditions before the task may run
     */
    public void schedule(String id, Runnable task, String... conditions) {
        schedule(new ScheduledTask(id, task, conditions));
    }

    /**
     * Schedules the task to be executed when all conditions are fulfilled.
     * If the task has no condition, it will be executed at {@link #start()}.
     *
     * @param task the task to be scheduled
     */
    public void schedule(ScheduledTask task) {
        tasks.add(task);
    }

    // This method is synchronized to prevent multiple concurrent trigger of the same task
    private synchronized void execute(ScheduledTask task) {
        // Only a waiting task can be executed
        if (task.getState() != ScheduledTaskState.WAITING)
            return;
        task.setState(ScheduledTaskState.LAUNCHING);
        // The .execute() method asks for execution but real execution can be delayed if no thread is currently available
        executor.execute(() -> {
            try {
                task.setState(ScheduledTaskState.RUNNING);
                task.run();
                task.setState(ScheduledTaskState.FINISHED);
                // The synchronized block prevents ConcurrentModificationException
                synchronized (tasks) {
                    // Once the task has been run, we remove it from our list
                    // This is not strictly necessary, but avoid the need to loop on every
                    // task and check their state to find out whether some task remains or not
                    tasks.remove(task);
                }
                // Signals that the task finished to trigger any dependent task
                conditionFulfilled(task.getId());
                // If this was the last task, we wake up the main thread
                // The synchronized block is mandatory to take ownership of the lock
                synchronized (lock) {
                    // The synchronized block prevents ConcurrentModificationException
                    synchronized (tasks) {
                        if (tasks.isEmpty())
                            lock.notifyAll();
                    }
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

    /**
     * Signals the fulfillment of a condition.
     * If this condition was the last one of the task, it will be executed.
     *
     * @param condition the fulfilled condition
     */
    public void conditionFulfilled(String condition) {
        // The synchronized block prevents ConcurrentModificationException
        synchronized (tasks) {
            for (ScheduledTask task : tasks) {
                Set<String> conditions = task.getConditions();
                conditions.remove(condition);
                // If this was the last condition, the task may now run
                if (conditions.isEmpty())
                    execute(task);
            }
        }
    }

    /**
     * Starts this scheduler.
     * Any task scheduled without condition will be executed right now.
     */
    public void start() {
        // Simulates a condition fulfill to trigger tasks without any condition
        this.conditionFulfilled(null);
    }

    /**
     * Stops the underlying executor service.
     *
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Pauses this thread until all tasks are over, or a single task fails.
     *
     * @param timeout the maximum amount of time before timing out
     * @param unit the unit of time for the timeout
     * @throws InterruptedException if the thread is interrupted
     * @throws TaskFailedException if a task fails
     */
    public void waitUntilAllTasksFinished(long timeout, TimeUnit unit) throws InterruptedException, TaskFailedException {
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
}

package com.ignfab.minalac.generator.utils.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;

/**
 * A scheduler is a service managing execution of tasks.
 * It can perform parallel operations and ensure execution order between some tasks.
 * A context object of generic type T is passed to executed tasks.
 *
 * @param <T> tasks execution context type
 */
public class Scheduler<T> {
    // An executor using a thread pool to run tasks
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, ScheduledTask<T>> tasks = new HashMap<>();
    // Object used for communication between the main thread and task threads.
    // .wait() / .notify() operations are performed on this object
    private final Object lock = new Object();

    /**
     * Schedules the task to be executed when all dependencies are finished.
     * If the task has no dependency, it will be executed at {@link #run(context, long, TimeUnit)}.
     *
     * @param id the ID of the task
     * @param task the task to be scheduled
     */
    public void schedule(String id, Consumer<T> task) {
        schedule(new ScheduledTask<T>(id, task));
    }

    /**
     * Schedules the task to be executed when all dependencies are finished.
     * If the task has no condition, it will be executed at {@link #run(context, long, TimeUnit)}.
     *
     * @param task the task to be scheduled
     */
    public void schedule(ScheduledTask<T> task) {
        if (tasks.containsKey(task.id()))
            throw new IllegalArgumentException("Task %s already scheduled".formatted(task.id()));
        tasks.put(task.id(), task);
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
     * This method can be used multiple times meaning the same set of tasks can be re-executed.
     * When waiting for task completion, this thread is paused until all tasks are over, a single task fails, or the wait timed out.
     * Following an exception there is an attempt to interrupt underlying tasks.
     *
     * @param context execution context passed to tasks
     * @param timeout the maximum amount of time before timing out
     * @param unit the unit of time for the timeout
     * @throws TaskFailedException if a task fails
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException if the wait timed out
     * @throws GenerationFailedException for any other unexpected exceptions during execution
     */
    public void run(T context, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, GenerationFailedException, TaskFailedException {
        tasks.values().forEach(ScheduledTask::reset);
        Future<Void> future = executor.submit(() -> launchTasks(context));
        try {
            future.get(timeout, unit);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException re)
                throw re;
            if (e.getCause() instanceof TaskFailedException tfe)
                throw tfe;
            throw new GenerationFailedException(e.getCause());
        } finally {
            // Attempt to interrupt any ongoing task in case of an error
            tasks.values().forEach(ScheduledTask::cancel);
        }
    }

    // The method submit() of ExecutorService can take either a Runnable or Callable as parameter.
    // Unlike Runnable, Callable can throw a checked exception, which is useful to easily propagate exceptions.
    // This method signature return type is a Void solely to conform with the Callable interface.
    private Void launchTasks(T context) throws InterruptedException, TaskFailedException {
        while (true) {
            boolean hasRunningTasks = false;
            boolean hasWaitingTasks = false;
            boolean hasRemainingTasks = false;

            // There is a possibility that a task finishes before this thread has a chance to wait for notify()
            // The whole block is synchronized to prevent that
            synchronized (lock) {
                for (ScheduledTask<T> task : tasks.values()) {
                    switch (task.state()) {
                        case FINISHED -> {}
                        case FAILED -> throw task.error();
                        case WAITING -> {
                            hasRemainingTasks = true;
                            if (task.tryExecute(context, executor, lock))
                                hasRunningTasks = true;
                            else
                                hasWaitingTasks = true;
                        }
                        case LAUNCHING, RUNNING -> {
                            hasRunningTasks = true;
                            hasRemainingTasks = true;
                        }
                    }
                }

                if (hasWaitingTasks && !hasRunningTasks)
                    throw new IllegalStateException("Deadlock detected: some tasks are waiting and can not be started");

                if (!hasRemainingTasks)
                    break;
                lock.wait();
            }
        }
        return null;
    }

    /**
     * Stops the underlying executor service.
     *
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        executor.shutdown();
    }

    private ScheduledTask<T> getTask(String id) {
        if (!tasks.containsKey(id))
            throw new IllegalArgumentException("Task %s doesn't exist".formatted(id));
        return tasks.get(id);
    }
}

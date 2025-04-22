package com.ignfab.minalac.generator.utils.execution;

import java.util.HashSet;
import java.util.Set;

/**
 * A task registered in the {@link Scheduler}.
 */
public class ScheduledTask {
    private final String id;
    private final Runnable task;
    private final Set<ScheduledTask> dependencies;
    private final Set<ScheduledTask> dependents;
    private ScheduledTaskState state = ScheduledTaskState.WAITING;


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
        this.dependents = new HashSet<>();
    }

    /**
     * Returns the ID of the task.
     *
     * @return the ID of the task
     */
    public String getId() {
        return id;
    }

    /**
     * Runs this task's runnable.
     * This method will return when the runnable returns.
     */
    public void run() {
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), "Starting " + id);
        task.run();
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), id + " finished");

    }

    /**
     * Returns the tasks that need to be completed before this task can be run.
     *
     * @return the dependencies tasks
     */
    public Set<ScheduledTask> getDependencies() {
        return dependencies;
    }

    /**
     * Returns the tasks that can only be executed if the task is completed.
     *
     * @return tasks that depends on this task
     */
    public Set<ScheduledTask> getDependents() {
        return dependents;
    }

    /**
     * Adds a task that needs to be completed before this task can be run.
     *
     * @param dependency the dependency task
     */
    protected void addDependency(ScheduledTask dependency) {
        this.dependencies.add(dependency);
        dependency.dependents.add(this);
    }

    /**
     * Returns the state of this task.
     *
     * @return the state of this task
     */
    public ScheduledTaskState getState() {
        return state;
    }

    /**
     * Sets the state of this task.
     *
     * @param state the new state of the task
     */
    public void setState(ScheduledTaskState state) {
        this.state = state;
    }
}

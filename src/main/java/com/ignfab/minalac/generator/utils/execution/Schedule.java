package com.ignfab.minalac.generator.utils.execution;

/**
 * A schedule that contains tasks and dependancies.
 */
public interface Schedule {
    /**
     * Adds task to the schedule with given identifier.
     *
     * @param id the identifier of the task
     * @param task the task to be scheduled
     */
    void addTask(String id, Runnable task);

    /**
     * Makes {@code idTask} dependent on the completion of the specified {@code idDependency} task.
     *
     * @param idTask the identifier of the task
     * @param idDependency the identifier the dependency
     */
    void addDependency(String idTask, String idDependency);
}

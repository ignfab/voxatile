package com.ignfab.minalac.generator.utils.execution;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * A task registered in the {@link Scheduler}.
 */
public class ScheduledTask {
    private final String id;
    private final Consumer<VoxelWorldTile> task;
    private final Set<String> conditions;
    private ScheduledTaskState state = ScheduledTaskState.WAITING;

    /**
     * Creates a new task with the given characteristics.
     *
     * @param id the ID of the task
     * @param task the runnable to execute
     * @param conditions the IDs of the tasks that need to complete before this one can run
     */
    public ScheduledTask(String id, Consumer<VoxelWorldTile> task, String... conditions) {
        this(id, task, Arrays.asList(conditions));
    }

    /**
     * Creates a new task with the given characteristics.
     *
     * @param id the ID of the task
     * @param task the runnable to execute
     * @param conditions the IDs of the tasks that need to complete before this one can run
     */
    public ScheduledTask(String id, Consumer<VoxelWorldTile> task, Collection<String> conditions) {
        this.id = id;
        this.task = task;
        if (conditions == null)
            conditions = Collections.emptyList();

        // Synchronized collections are thread-safe equivalent of regular collections
        // This is important to ensure no problem occurs if two required tasks finishes at the same time
        this.conditions = Collections.synchronizedSet(new HashSet<>(conditions));
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
     *
     * @param tile Tile on which task is run
     */
    public void run(VoxelWorldTile tile) {
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), "Starting " + id);
        task.accept(tile);
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), id + " finished");
    }

    /**
     * Returns the IDs of the tasks that need to complete before this one can run.
     *
     * @return the conditions of this task
     */
    public Set<String> getConditions() {
        return conditions;
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

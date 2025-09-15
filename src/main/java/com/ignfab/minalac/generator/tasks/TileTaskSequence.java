package com.ignfab.minalac.generator.tasks;

import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;

/**
 * A tile task that runs other tile tasks in sequence and in order.
 */
public class TileTaskSequence implements TileTask {
    private List<TileTask> tasks = new LinkedList<>();

    /**
     * Adds a {@link TileTask} at the end of the sequence.
     *
     * @param task task to add
     */
    public void add(TileTask task) {
        tasks.add(task);
    }

    @Override
    public void run(GenerationTile tile) {
        for (TileTask task : tasks)
            task.run(tile);
    }
}

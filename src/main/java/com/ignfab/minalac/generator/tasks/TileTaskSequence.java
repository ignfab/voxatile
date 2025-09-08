package com.ignfab.minalac.generator.tasks;

import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;

public class TileTaskSequence implements TileTask {
    List<TileTask> tasks = new LinkedList<>();

    public TileTaskSequence() {
    }

    public void add(TileTask task) {
        tasks.add(task);
    }

    @Override
    public void run(GenerationTile tile) {
        for (TileTask task : tasks)
            task.run(tile);
    }
}

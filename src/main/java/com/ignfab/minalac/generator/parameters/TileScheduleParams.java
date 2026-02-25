package com.ignfab.minalac.generator.parameters;


import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.parameters.tasks.generic.NamedTaskListParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Scheduler;

/**
 * Parameters for a tile task schedule.
 */
public class TileScheduleParams extends NamedTaskListParams<GenerationTile> {

    /**
     * Creates a {@link Scheduler} launching this schedule out of parameters.
     *
     * @param generation Generation for which create that scheduler.
     * @return resulting {@link Scheduler}.
     */
    public Scheduler<GenerationTile> create(Generation generation) {
        Scheduler<GenerationTile> scheduler = new Scheduler<>();

        Map<String, TaskParams<GenerationTile>> flatTasks = new HashMap<>();
        tasks.forEach((name, task) -> { flatTasks.putAll(task.flatten(name)); });

        // Create tasks
        flatTasks.forEach((name, task) -> {
            scheduler.schedule(name, task.create(generation));
        });

        // Once all tasks created, we can add dependencies
        flatTasks.forEach((name, task) -> {
            for (String after : task.after)
                scheduler.addDependency(name, after);
        });

        return scheduler;
    }
}

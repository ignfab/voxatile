package com.ignfab.minalac.generator.parameters;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.parameters.tasks.TileTaskParams;
import com.ignfab.minalac.generator.utils.execution.Scheduler;

/**
 * Parameters for a tile task schedule.
 */
public class TileScheduleParams extends HashMap<String, TileTaskParams> {

    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if schedule parameter is invalid.
     */
    public void validate() {
        for (TileTaskParams task : values())
            task.validate();
    }

    /**
     * Creates a {@link Scheduler} launching this schedule out of parameters.
     *
     * @param generation Generation for which create that scheduler.
     * @return resulting {@link Scheduler}.
     */
    public Scheduler<GenerationTile> create(Generation generation) {
        Scheduler<GenerationTile> scheduler = new Scheduler<>();

        // Flatten schedule params into tasks map
        Map<String, TileTaskParams> tasks = new HashMap<>();
        forEach((name, task) -> {
            tasks.putAll(task.createAditionalTaskParams(name));
            tasks.put(name, task);
        });

        // Create tasks
        tasks.forEach((name, taskParams) -> scheduler.schedule(name, taskParams.create(generation)));

        // Once all tasks created, we can add dependencies
        tasks.forEach((name, taskParams) -> taskParams.after.forEach(after -> scheduler.addDependency(name, after)));

        return scheduler;
    }
}

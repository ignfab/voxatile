package com.ignfab.minalac.generator.parameters;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.tasks.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Scheduler;

/**
 * Parameters for a tile task schedule.
 */
public class ScheduleParams extends LinkedHashMap<String, TaskParams> {

    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if schedule parameter is invalid.
     */
    public void validate() {
        for (TaskParams task : values())
            task.validate();
    }

    /**
     * Creates a {@link Scheduler} launching this schedule out of parameters.
     *
     * @param generation Generation for which create that scheduler.
     * @return resulting {@link Scheduler}.
     */
    public Scheduler create(Generation generation) {
        Scheduler scheduler = new Scheduler();

        // Flatten schedule params into tasks map
        Map<String, TaskParams> tasks = new HashMap<>();
        forEach((name, task) -> {
            tasks.putAll(task.flatten(name));
        });

        // Create tasks
        tasks.forEach((name, taskParams) -> scheduler.schedule(name, taskParams.create(generation)));

        // Once all tasks created, we can add dependencies
        tasks.forEach((name, taskParams) -> {
            for (String after : taskParams.after)
                scheduler.addDependency(name, after);
        });

        return scheduler;
    }
}

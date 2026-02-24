package com.ignfab.minalac.generator.parameters.tasks.generic;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

/**
 * TODO
 *
 *
 * TODO: NamedTasksListParams is more or less a HashMap. Compisition has been chosen but inheritance could do the job. Both choices have drawbacks:
 * Inheritance : we need to be sure no setters could bypass TileScheduleParams checkings.
 * Composition: we have to proxy all getters and setters or use .tasks member.
 */
public class NamedTaskListParams<T> {
    @JsonIgnore // Or task named "tasks" will not call setter (put)
    public HashMap<String, TaskParams<T>> tasks = new HashMap<>();

    /**
     * Puts a new task in list.
     *
     * @param name Name of the task to add
     * @param task Parameters of task to add
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonAnySetter
    public void put(String name, TaskParams<T> task) {
        TaskParams.validateName(name);

        // This should not happen in Jackson context as duplicate keys are invalid in Yaml.
        // But this method could be used outside that context.
        if (tasks.containsKey(name))
            throw new IllegalArgumentException("Duplicate task name \"%s\".".formatted(name));

        tasks.put(name, task);
        tasks.putAll(task.createAdditionalTaskParams(name));
    }

    public void validate() {
        tasks.values().forEach((t) -> t.validate());
    }
}

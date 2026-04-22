package com.ignfab.minalac.generator.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.tasks.TaskParams;
import com.ignfab.minalac.generator.utils.execution.Schedule;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a tile task schedule.
 */
public class ScheduleParams {

    /**
     * Tasks, indexed by name, in this schedule.
     * <p>
     * This field is not deserialized, we use `@JsonAnySetter` for the whole deserialization.
     * It is package public for test purposes.
     */
    @JsonIgnore // Or task named "tasks" will not call setter (put)
    /*package public*/ Map<String, TaskParams> tasks = new LinkedHashMap<>();

    /**
     * Puts a new task in list.
     * <p>
     * This is the only, and default, setter for all keys.
     * This is a trick to makes {@code ScheduleParams<T>} behave like a {@code Map<String, TaskParams<T>>}.
     *
     * @param name Name of the task to add
     * @param task Parameters of task to add
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonAnySetter
    public void put(String name, TaskParams task) {
        TaskParams.validateTaskName(name);

        // This should not happen in Jackson context as duplicate keys are invalid in Yaml.
        // But this method could be used outside that context.
        if (tasks.containsKey(name))
            throw new IllegalArgumentException("Duplicate task name \"%s\".".formatted(name));

        tasks.put(name, task);
    }

    /**
     * Flattens schedule params.
     * <p>
     * Schedule params may have compisite tasks with subtasks. After `flatten`, it will only contain tasks at the main level,
     * with translated names and dependancies, so it works exactly the same. This step is required in order to be able to create actual schedule.
     */
    /*public void flatten() {
        Map<String, TaskParams> flat = new HashMap<>();
        tasks.forEach((name, task) -> flat.putAll(task.flatten(name)));
        tasks = flat;
    }*/

    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if schedule parameter is invalid.
     */
    public void validate() {
        tasks.values().forEach(TaskParams::validate);
    }

    /**
     * Populates an existing {@link Scheduler} with tasks from this {@link ScheduleParams}.
     *
     * @param generation Generation for which create that scheduler
     * @param schedule {@link Schedule} to populate
     */
    public void populate(Generation generation, Schedule schedule) {
        // Flatten schedule
        //flatten();
        Tasks tasks = new Tasks();
        this.tasks.forEach((name, task) -> task.populate(tasks, name, tasks, Set.of(), new ModelSelectionParams()));

        // Create actual tasks
        for (TaskInfo task : tasks)
            schedule.addTask(task.id(), task.create(generation));

        // Once all tasks created, we can add dependencies
        for (TaskInfo task : tasks)
            for (TaskInfo dep : task.dependencies())
                schedule.addDependency(task.id(), dep.id());
    }

    public record TaskInfo(String id, Function<Generation, Task> creator, Set<TaskInfo> dependencies) {
        public Task create(Generation generation) {
            return creator.apply(generation);
        }
    }

    public static class Tasks implements DependencyResolver, Iterable<TaskInfo> {
        private final Map<String, TaskInfo> tasks = new HashMap<>();

        public void add(TaskInfo task) {
            tasks.put(task.id(), task);
        }

        @Override
        public TaskInfo optional(String name) {
            return tasks.get(name);
        }

        @Override
        public Iterator<TaskInfo> iterator() {
            return tasks.values().iterator();
        }
    }

    @FunctionalInterface
    public interface DependencyResolver {
        TaskInfo optional(String name);

        default TaskInfo required(String name) {
            TaskInfo resolved = optional(name);
            if (resolved == null)
                throw new IllegalArgumentException("Unknown dependency: " + name);
            return resolved;
        }

        default DependencyResolver onlyInside(Collection<String> names) {
            return filtered(names::contains);
        }

        default DependencyResolver filtered(Predicate<String> filter) {
            return new FilteredDependencyResolver(this, filter);
        }

        default DependencyResolver namespaced(String namespace) {
            return new NamespacedDependencyResolver(this, namespace);
        }

        default DependencyResolver fallback(DependencyResolver fallback) {
            return new FallbackDependencyResolver(this, fallback);
        }
    }

    public record FilteredDependencyResolver(DependencyResolver delegate, Predicate<String> filter) implements DependencyResolver {
        @Override
        public TaskInfo optional(String name) {
            return filter.test(name) ? delegate.optional(name) : null;
        }
    }

    public record NamespacedDependencyResolver(DependencyResolver delegate, String namespace) implements DependencyResolver {
        @Override
        public TaskInfo optional(String name) {
            return delegate.optional(namespace + TaskParams.SEPARATOR + name);
        }
    }

    public record FallbackDependencyResolver(DependencyResolver delegate, DependencyResolver fallback) implements DependencyResolver {
        @Override
        public TaskInfo optional(String name) {
            TaskInfo resolved = delegate.optional(name);
            return resolved == null ? fallback.optional(name) : resolved;
        }
    }
}

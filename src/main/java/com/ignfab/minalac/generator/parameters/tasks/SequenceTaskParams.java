package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a task running other tasks one by one in sequence.
 * <p>
 * This param class does only instanciate a {@link NoOperationTask} but it {@link createAditionalTaskParams}
 * for all subtasks.
 */
public class SequenceTaskParams extends ModelTaskParams {

    /**
     * List of subclasses in run order.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public List<TileTaskParams> subtasks;

    /**
     * List of imported dependencies (name of external task that could be used in subtasks after).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Set<String> using = new HashSet<>();

    /**
     * Creates a new {@code SequenceTaskParams} with required fields.
     *
     * @param subtasks list of subtasks in run order
     */
    @ConstructorProperties({"do"})
    public SequenceTaskParams(List<TileTaskParams> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public void validate() {
        super.validate();

        using.forEach(TileTaskParams::validateTaskName);

        subtasks.forEach(subtask -> subtask.validate());
    }

    @Override
    public Map<String, TileTaskParams> createAditionalTaskParams(String prefix) {
        Map<String, TileTaskParams> result = new HashMap<>();

        int index = 0;
        for (TileTaskParams subtask : subtasks) {
            if (index == 0)
                // First task starts after sequence afters
                subtask.after.addAll(after);
            else
                // Other tasks start after their previous task
                subtask.after.add(prefix + SEPARATOR + index);

            index++;

            String subname = prefix + SEPARATOR + index;

            result.put(subname, subtask);

            // getAditionalTaskParams is supposed to prefix all its results with subname
            // so we can presume no result will be overwritten.
            result.putAll(subtask.createAditionalTaskParams(subname));
        }

        // Merge model selections
        result.forEach((name, task) -> {
            if (task instanceof ModelTaskParams modelTask)
                modelTask.models.narrowDown(models);
        });

        // Sequence task (which is a noop marker) starts after last task
        after = Set.of(prefix + SEPARATOR + index);

        return result;
    }

    @Override
    public TileTask create(Generation generation) {
        return NoOperationTask.INSTANCE;
    }

}

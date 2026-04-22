package com.ignfab.minalac.generator.parameters.tasks;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.ScheduleParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.utils.execution.Task;

public abstract class SimpleTaskParams extends TaskParams {
    @Override
    public void populate(ScheduleParams.Tasks tasks, String fqn, ScheduleParams.DependencyResolver resolver, Set<ScheduleParams.TaskInfo> additionalDependencies, ModelSelectionParams inheritedModels) {
        tasks.add(new ScheduleParams.TaskInfo(
            fqn,
            this::create,
            Stream.concat(after.stream().map(resolver::required), additionalDependencies.stream()).collect(Collectors.toSet())
        ));
    }

    public abstract Task create(Generation generation);
}

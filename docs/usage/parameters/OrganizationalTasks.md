# Organizational tasks

Some tasks don't perform any generation action but only have organizational purpose.

Three organizational tasks are available:

* [`noOperation`](#nooperation) does not do anything;
* [`sequence`](#sequence) runs subtasks in sequence;
* [`schedule`](#schedule) runs subtasks in a subschedule;

`sequence` and `schedule` task types share some [common behavior](#common-behavior):
* [Dependencies to other tasks](#dependencies-to-other-tasks)
* [External dependencies](#using-external-dependencies)
* [Model selections](#model-selections)

## `noOperation`

Does nothing. Its only purpose is to wait for other tasks and gather them under one generic name.

#### Example
```yaml
allBuildsDone:
  type: noOperation
  after:
    - buildWalls
    - buildRoof
    - buildFloors
```
This task can be used in `after` fields of other tasks needing buildings to be set up before they start.

## `sequence`

A task that runs other tasks in sequence.

`sequence` task type has some [common behavior](#common-behavior) with `schedule` task type.

### Extra parameters

* `models`: A (maybe partial) [selection of models](ModelSelection.md) which will be inherited by all subtasks (defined in `do`). If none given or partially defined, each subtask must provide its own.
* `do`: A list of subtasks to launch in sequence. Subtasks are not named hence they cannot be referred to in any `after` field.
* `using`: A list of tasks that subtasks (defined in `do`) can refer to as dependencies.

### Examples

Describing a succession of tasks could be tedious:
```yaml
task1:
  type: type1
  models:
    ...
  ...
task2:
  type: type2
  after: task1
  models:
    ... (same as task1)
  ...
task3:
  type: type3
  after: task2
  models:
    ... (same as task1 and task2)
  ...
... (more tasks in succession)
```

A `sequence` type task offers a simplified way to describe such processes. It includes a list of subtasks that are run in sequence and may share the same model selection.

Here is an example describing the same process as above:
```yaml
sequence1:
  type: sequence
  models:
    ... (same as above task1)
  do:
    - type: type1
      ... (same as task1 but without after and models tags)
    - type: type2
      ...
    - type: type3
      ...
    ... (more tasks in succession)

othertask:
  after: sequence1
...
```

All subtasks in `do` (they don't have names because they don't need to) will be run one after the other. They will all inherit model selection from `sequence1`.

Here `othertask` will run after the last subtask of the sequence.


## `schedule`

A task that runs other tasks in parallel with optional dependencies between them.

`schedule` task type has some [common behavior](#common-behavior) with `sequence` task type.

### Extra parameters

* `models`: A (maybe partial) [selection of models](ModelSelection.md) which will be inherited by all subtasks (defined in `do`). If none given or partially defined, each subtask must provide its own.
* `do`: Subtasks to launch, indexed by name. Names must be unique within this schedule task. They are defined with their own scope, so they won't collide with global tasks, except those declared in `using` field.
* `using`: A list of tasks that subtasks (defined in `do`) can refer to as dependencies.

### Examples

Unlike `sequence` that is intended to simplify consecutive tasks description, `schedule` is used to gather related tasks.
This may simplify dependencies (a single "after" referring to the schedule task ensures all subtasks are finished) and model selection (that could be shared by subtasks).

```yaml
sometask:
  ...

schedule1:
  after: sometask
  type: schedule
  do:
    subtask1:
      ...
    subtask2:
      ...
    subtask3:
      after:
        - subtask1
        - subtask2
      ...
    subtask4:
      after: subtask1

othertask:
  after: schedule1
...
```

The four subtasks of `schedule1` will run after `sometask`, respecting the `after` constraints. `othertask` will start once all subtasks have ended.

## Common behavior

Both `sequence` and `schedule` shares some common behavior.

### Dependencies to other tasks

Like any other task, `sequence` and `schedule` tasks (and so their subtasks) will start only when all tasks mentioned in their respective `after` field have finished.

Likewise, any other task that refers to a `schedule` or `sequence` task in its `after` list will start once that `schedule` or `sequence` task have ended (i.e. once all subtasks have ended).

Subtasks of a `schedule` or `sequence` task could never be used as dependencies outside that `schedule` or `sequence` task.

### Using external dependencies

By default, subtasks cannot depend on a task outside their parent `schedule` or `sequence` task.
It could be useful to have some subtasks depending on an external task, without having the whole `schedule` or `sequence` task depending on it.
This is made possible by declaring external tasks in `using` field. They can then be referred to in `after` field of subtasks.

A good example would be a `sequence` or `schedule` including a long task and having other tasks after that long task that also depends on another long task outside of the `sequence` or `schedule`:

```yaml
anotherLongTask:
  ...

schedule:
  after:
    - anotherLongTask # To be sure dependentTask does not run before anotherLongTask
    - ...
  do:
    veryLongTask:
      ...
    dependentTask: # This task must run after veryLongTask AND anotherLongTask
      after: veryLongTask
    ...
...
```

Suppose `dependentTask` depends on `anotherLongTask` but `veryLongTask` does not.
By declaring that `schedule` must run after `anotherLongTask`, we loose the opportunity to have `veryLongTask` and `anotherLongTask` run in parallel because `veryLongTask` is in the schedule which runs after `anotherLongTask` (because we want to make sure that `dependentTask` will not run before it ends).

In such case, the `using` field could be used to make `anotherLongTask` visible to subtasks and usable in `after` field:

```yaml
anotherLongTask:
  ...

schedule:
  using: anotherLongTask
  after:
    - ...
  do:
    veryLongTask:
      ...
    dependentTask:
      after:
        - veryLongTask
        - anotherLongTask
    ...
...
```

Now the schedule (including `veryLongTask`) can start without waiting for the end of `anotherLongTask`, but `dependentTask` will still wait for it.

### Model selections

Subtasks inherit the model selection from their parent `sequence` or `schedule` task.

A subtask may narrow its model selection down by also having a `models` field:
```yaml
sequence1:
  type: sequence
  models:
    type: buildings
  do:
    ...
    - type: ...
      models:
        filter:
           metadata: height
           equals: 0
      ...
```

It is also possible to have no model selection in `sequence` or `schedule` task and define entirely separate model selections in tasks.
All that matters is that, at the end, each subtask must have a valid (i.e. with a `type`) model selection before running.

Inheritance is performed by intersecting model selections from different levels. This may result in an empty selection contradictory filters are combined. It is not allowed to combine different types.

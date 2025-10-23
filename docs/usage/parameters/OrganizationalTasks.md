# Organizational tasks

Some tasks don't perform any generation action but have only organizational purpose.

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

### Example
```yaml
type: noOperation
after:
  - buildWalls
  - buildRoof
  - buildFloors
```
Such a task could be named `allBuildsDone` and used in `after` fields of other task needing buildings to be set up before they start.

## `sequence`

A task that runs other tasks in sequence.

`sequence` task type has some [common behavior](#common-behavior) with `schedule` task type.

### Extra parameters

* `models`: A (maybe partial) [selection of models](ModelSelection.md) which will be inherited by all subtasks. If none given or partially defined, each subtask should provide its own.
* `do`: A list of subtasks to launch in sequence. Subtasks are not named hence they cannot be referred in other subtasks `after` field.
* `using`: A list of tasks that subtask can refer to as dependencies.

### Examples

Describing process simply performing a succession of task could be tedious:
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

`sequence` type tasks offer a simplified way to describe such processes. They include a list of subtask that are run in sequence and share the same model selection.

Here is an example describing the same process as above:
```yaml
sequence1:
  type: sequence
  models:
    ... (same as above task1)
  do:
    - type: type1
      ... (same as task1 but without after and model tags)
    - type: type2
      ...
    - type: type3
      ...
    ... (more tasks in succession)

othertask:
  after: sequence1
...
```

All subtasks in `do` (they don't have names because they don't need to) will be run one after the other. They will all process models in `sequence1` model selection.

Here `othertask` will run after the last subtask in the sequence.


## `schedule`

A task that runs other tasks in parallel with optional dependencies between them.

`schedule` task type has some [common behavior](#common-behavior) with `sequence` task type.

### Extra parameters

* `models`: A (maybe partial) [selection of models](ModelSelection.md) which will be inherited by all subtasks. If none given or partially defined, each subtask should provide its own.
* `do`: A list of subtasks to launch, indexed by name.
* `using`: A list of tasks that subtask can refer to as dependencies.

### Examples

Unlike `sequence` that is intended to simplify consecutive tasks description, `schedule` is more likely to be used gather related tasks. This may simplify dependencies (a single "after" schedule task ensure all subtasks are finished) and model selection (that could be shared by subtasks).

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

The four subtask of `schedule1` will run after `sometask`, respecting the `after` constraints. `othertask` will start once all subtasks have ended.

## Common behavior

Both `sequence` and `schedule` shares some common behavior.

### Dependencies to other tasks

Like any other tasks, `sequence` and `schedule` tasks (and so their subtasks) will start only when all tasks mentioned in their `after` fields have finished.

And any other task that have a `schedule` or `sequence` task in its `after` list will start once that `schedule` or `sequence` task have ended. Which means once all subtasks have ended.

Tasks from a `schedule` or `sequence` could never be used as dependencies outside that `schedule` or `sequence`.

### Using external dependencies

By default `schedule` or `sequence` subtasks can not depend on a task outside that `schedule` or `sequence`.
But some cases, it could be useful to have a subtask depending on an external task.

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
    dependentTask: # This tasks must run after veryLongTask AND anotherLongTask
      after: veryLongTask
    ...
...
```

If `dependentTask` depends on `anotherLongTask` but `veryLongTask` does not,
we loose the opportunity to have `veryLongTask` and `anotherLongTask` running in parallel because `veryLongTask` is in the schedule which runs after `anotherLongTask` (because we want to make sure `dependentTask` will not run before it ends).

In such case, the `using` field could be used to somehow import `anotherLongTask` into schedule and make it available in subtasks `after` field:

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

Now the schedule, and so `veryLongTask`, can start without waiting for `anotherLongTask` to end. But `dependentTask` will still wait for it.

### Model selections

Subtasks model selection is inherited from `sequence` or `schedule` task.

A subtask may narrow down its model selection by also having an `models` field:
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

It is also possible to have no model selection in `sequence` or `schedule` task and entirely define separate model selections in tasks.
The only rule with model selections is that, at the end, subtasks should have valid (i.e. with a `type`) model selections before running.
If several different types are given at different levels, this will result in an empty model selection. Same if contradictory filters are combined.

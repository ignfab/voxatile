# Tasks

*[Tile Tasks](TileTasks.md)* are performed on each individual tile and must be used within a "tile schedule" (currently `forEachTile` only).

*Tasks* are independent of tiles and can be used anywhere.

Generic schedules such as the `afterAllTiles` schedule can only execute generic *Tasks* and not *Tile Tasks*.

Each task has a `type`, optional dependencies on other tasks (using `after`), and specific parameters depending on its type.

## Dependencies

Dependencies allow to control execution order of tasks. Optional `after` field lists names of every other tasks that should run before the task it belongs to.

For example, if we want `renderBuildings` to run after `fetchBuildings` and `renderAltitude` tasks, we write:
```yaml
forEachTile:
  renderBuildings:
    after:
      - fetchBuildings
      - renderAltitude
    ...
```

**BEWARE** : For now, `after` values are not checked. Bad configurations may induce deadlock that will make generation fail.

### Scope constraints

A task can only depend on other tasks that belong to the same execution scope. Dependencies cannot cross the boundary between different scopes (e.g., a task in `afterAllTiles` cannot depend on a task in `forEachTile`).

#### Valid configuration (Same scope):

```yaml
# Valid: tasks within the same scope can depend on each other
forEachTile:
  a:
    type: noOperation
  b:
    after: a
    type: noOperation
# Valid
afterAllTiles:
  c:
    type: noOperation
  d:
    after: c
    type: noOperation
```

#### Invalid configuration (Cross-scope):

```yaml
# Invalid: 'afterAllTiles' cannot see tasks defined in 'forEachTile'
forEachTile:
  a:
    type: noOperation
afterAllTiles:
  b:
    after: a # Error: 'a' is not defined in this scope
    type: noOperation
```

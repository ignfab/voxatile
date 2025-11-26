# Tasks

*[Tile Tasks](TileTasks.md)* are performed on each individual tile and must be used within a "tile schedule" (currently `forEachTile` only).

*Tasks* are independent of tiles and can be used anywhere.

Generic schedules such as the `afterAllTiles` schedule can only execute generic *Tasks* and not *Tile Tasks*.

Each task has a `type`, optional dependencies on other tasks (using `after`), and specific parameters depending on its type.

## Table of contents

* [Dependencies](#dependencies)
  * [Scope constraints](#scope-constraints)
* [Tasks operating on minimaps](#tasks-operating-on-minimaps)
  * [`applyShadingMinimap`](#applyshadingminimap)
  * [`saveMinimap`](#saveminimap)

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

## Tasks operating on minimaps

### `applyShadingMinimap`

Applies post-processing shading to a minimap using its heightmap.

This task simulates sunlight by calculating the directional terrain slope, adjusting pixel brightness according to how the terrain is oriented relative to a light source.
This enhances the visual perception of elevation changes.

#### Extra parameters

- `minimap` (required): Name of the minimap to shade.
- `shadowIntensity` (optional, default is `0.5`): The intensity of the shading effect (should be between 0.0 and 1.0).
- `sunDirection` (optional, default is `90`): The direction of the simulated light source, expressed in degrees.

#### Example

```yaml
type: applyShadingMinimap
minimap: overworld
shadowIntensity: 0.8
sunDirection: 45
```

### `saveMinimap`

Export the Minimap to an image file.

The `saveMinimap` task requires the `populateMinimap` task to be executed on each tile beforehand. `populateMinimap` generates the actual map data; without it, the saved image will be empty.

#### Extra parameters

- `minimap` (required): name of the minimap to save.
- `destination` (required): relative path where the rendered minimap image will be saved. This path is relative to the world's root directory.
- `format` (optional): format of the minimap to render (ex: `jpeg`, `png`, `gif`, ...).

#### Exemple

```yaml
type: saveMinimap
minimap: overworld
destination: overworld-minimap.png
format: png
```

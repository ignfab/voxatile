# Tile tasks

Tile tasks will be performed on each tile. They only can be used in a "tile schedule" (for now `forEachTile` only).

Each task has a `type`, optional dependencies to other tasks (in `after`), and other parameters depending on its type.

## Table of contents

* [Organizational tasks](#organizational-tasks)
* [Tasks fetching data](#tasks-fetching-data)
  * [`fetchData`](#fetchdata)
* [Tasks operating on world](#tasks-operating-on-world)
  * [`renderHeightmap`](#renderheightmap)
  * [`renderSurfaces`](#rendersurfaces)
  * [`fillBetweenHeightmapAndMetadata`](#fillbetweenheightmapandmetadata)
  * [`renderBuildings`](#renderbuildings)
  * [`setSpawn`](#setspawn)
  * [`renderVoxels`](#rendervoxels)
  * [`findVoxels`](#findvoxels)
* [Tasks operating on heightmaps](#tasks-operating-on-heightmaps)
  * [`populateHeightmap`](#populateheightmap)
  * [`copyHeightmap`](#copyheightmap)
  * [`setHeightmap`](#setheightmap)
  * [`computeHeightmapStats`](#computeheightmapstats)

## Organizational tasks

### `noOperation`

Does nothing. Its only purpose is to wait for other tasks and gather them under one generic name.

#### Example
```yaml
type: noOperation
after:
  - buildWalls
  - buildRoof
  - buildFloors
```
Such a task could be named `allBuildsDone` and used in `after` fields of other task needing buildings to be set up before they start.

## Tasks fetching data

### `fetchData`

Fetches data to be processed. Data may be fetched from various sources.

Parameters for `fetchData` task are described in [FetchDataTask.md](FetchDataTask.md) file.

## Tasks operating on world

These tasks may sometimes be referred as *renderers* as they render voxels (usually from models).

### `renderHeightmap`

Places [placeables](Placeables.md) on one heightmap (`at`) or between two (`minimum` and `maximum`).

#### Extra parameters
- `minimum`: The minimum [heightmap](Heightmaps.md) to use.
- `maximum`: The maximum [heightmap](Heightmaps.md) to use.
- `at`: The [heightmap](Heightmaps.md) to use.
- `place`: The [placeable](Placeables.md) to place at each heightmap voxel.

**NOTE**: `at` cannot be used with `minimum` or `maximum` (when `at` is absent, `minimum` and `maximum` must be used together).

#### Example

This will place grass voxels at the height of the specified heightmap. For better rendering, place voxel structure instead of a single voxel.

```yaml
type: renderHeightmap
at: ground
place: default:grass
```

This will place water voxels between the water and ground heightmaps.
```yaml
type: renderHeightmap
minimum: water
maximum: ground
place: default:water
```

### `renderSurfaces`

Renders 2-D shapes surfaces with [placeables](Placeables.md) on a given [heightmap](Heightmaps.md).

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to render (required, models must be voxelizable in 2d)
- `heightmap`: [Heightmap](Heightmaps.md) to use (required)
- `place`: [Placeable](Placeables.md) to place on each voxel of shapes (required)

#### Examples

Draw "building" shapes with cobble on ground:
```yaml
type: renderSurfaces
models:
  type: building
heightmap: ground
place: default:cobble
```

### `fillBetweenHeightmapAndMetadata`

For each model in [selection](ModelSelection.md), fills the gap between a heightmap and an altitude (given by a model metadata) within the model's boundaries.

If the heightmap value is lower than or equal to the altitude, the placeable used to fill is `placeBelow`, otherwise it is `placeAbove`.

#### Extra parameters

- `models` (required, models must be voxelizable in 2d): [Selection of models](ModelSelection.md) to use.
- `heightmap` (required): [Heightmap](Heightmaps.md) to use.
- `altitudeMetadata` (required): Name of the model metadata containing the altitude value.
- `placeAbove` (optional, default [`Nothing`](Placeables.md#nothing)): [Placeable](Placeables.md) placed above the altitude value.
- `placeBelow` (optional, default [`Nothing`](Placeables.md#nothing)): [Placeable](Placeables.md) placed below the altitude value.

**NOTE**: It must have at least the `placeBelow` or `placeAbove` field specified

#### Example

```yaml
type: fillBetweenHeightmapAndMetadata
models:
  type: buildings
heightmap: ground
altitudeMetadata: maximum-ground-altitude
placeAbove: air
placeBelow: default:cobble
```

### `renderBuildings`

Renders buildings using selected models as buildings footprint.

Building height is given by `height` metadata (This task does nothing if the value is not an integer, missing, negative, or zero).

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to render (required, models must be voxelizable in 2d).
- `roof`: [Placeable](Placeables.md) used to render roofs.
- `wall`: [Placeable](Placeables.md) used to render walls.
- `window`: [Placeable](Placeables.md) used to render windows.

#### Required model metadata

- `height`: Height of the building to render (must be a positive integer). The height is defined as the distance between the minimum ground altitude and the gutter altitude.
- `minimum-ground-altitude`: Minimum altitude inside the shape of the model.
- `ground-floor-altitude`: Ground floor altitude of the model.

#### Example

```yaml
type: renderBuildings
models:
  type: buildings
roof: default:cobble
wall: default:brick
window: default:glass
```

### `setSpawn`
This task sets the initial position of the player.
The provided coordinates must be within the generation limits.
It queries the heightmap to determine the altitude (Z) of the spawn point.

#### Extra parameters

- `heightmap`: [Heightmap](Heightmaps.md) to use (required).
- `x` (int): x-coordinate of the initial position (required).
- `y` (int): y-coordinate of the initial position (required).

#### Example

```yaml
type: setSpawn
heightmap: ground
x: 2
y: -1
```

### `renderVoxels`

Renders models by placing a voxel at each coordinate within the model.

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to render (required, models must be voxelizable in 3d)
- `place`: [Placeable](Placeables.md) used to render voxels.

#### Example

```yaml
type: renderVoxels
models:
  type: lidar
place: default:stone
```

### `findVoxels`

Finds the lowest and/or highest voxels placed over the model and adds results as metadata.

#### Extra parameters

- `models` (required, models must be 2d points): [Selection of models](ModelSelection.md) to use.
- `only` (optional, default none): [Placeable(s)](Placeables.md) used to select which voxels to match (can be a single voxel or a list).
- `except` (optional, default none): [Placeable(s)](Placeables.md) used to select which voxels not to match (can be a single voxel or a list).
- `find` (required): Specifies which voxels to find
  - `lowest` (optional): Metadata where to store the z-coordinate of the lowest voxel found.
  - `highest` (optional): Metadata where to store the z-coordinate of the highest voxel found.

When both `only` and `except` are absent, any voxel (including air) is matched.

**NOTE**: Fields `only` and `except` are mutually exclusive.

**NOTE**: At least one of `lowest` or `highest` field is required.

#### Example

```yaml
type: findVoxels
models:
  type: trees
only: default:leaves
find:
  highest: highest-leaves
```

## Tasks operating on heightmaps

### `populateHeightmap`

Populates a heightmap with models data. Existing data is overwritten.

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to use (required, models must be float matrices)
- `heightmap`: [Heightmap](Heightmaps.md) to populate, should be a [stored heightmap](Heightmaps.md#stored-heightmap) as it will be updated (required)

#### Example

```yaml
type: populateHeightmap
models:
  type: altitude
heightmap: ground
```

### `copyHeightmap`

Copies the values of a heightmap to another at all coordinates within the model's shape.

##### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to use as a filter (required, models must be voxelizable in 2d).
- `from`: [Heightmap](Heightmaps.md) to use.
- `to`: [Heightmap](Heightmaps.md) receiving the values. It should be a [stored heightmap](Heightmaps.md#stored-heightmap).

##### Example

```yaml
type: copyHeightmap
models:
  type: water
from: 1
to: water
```

### `setHeightmap`

Sets a value in a heightmap all coordinates within the model's shape.

##### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to use as a filter (required, models must be voxelizable in 2d).
- `value`: Value to set. This value can be fetched from model metadata.
- `to`: [Heightmap](Heightmaps.md) receiving the values. It should be a [stored heightmap](Heightmaps.md#stored-heightmap).

##### Example

```yaml
type: setHeightmap
models:
  type: batiment
from: base-altitude
to: ground
```

### `computeHeightmapStats`

Computes heightmap statistics over a model surface and adds the results as metadata.

#### Extra parameters

- `models` (required, models must be voxelizable in 2d): [Selection of models](ModelSelection.md) to use for computing statistics.
- `heightmap` (required): [Heightmap](Heightmaps.md) to use.
- `compute` (required): Specifies which statistics to compute
  - `maximum` or `max` (optional): Metadata where to store the computed maximum value.
  - `minimum` or `min` (optional): Metadata where to store the computed minimum value.

**NOTE**: The `compute` field must have at least one optional subfield specified.

#### Example

```yaml
type: computeHeightmapStats
models:
  type: building
heightmap: ground
compute:
  maximum: maximum-ground-altitude
  minimum: minimum-ground-altitude
```

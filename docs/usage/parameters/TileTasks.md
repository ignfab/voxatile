# Tile tasks

Tile tasks will be performed on each tile. They only can be used in a "tile schedule" (for now `forEachTile` only).

Each task has a `type`, optional dependencies to other tasks (in `after`), and other parameters depending on its type.

## Table of contents

* [Tasks fetching data](#tasks-fetching-data)
  * [`fetchData`](#fetchdata)
* [Tasks operating on world](#tasks-operating-on-world)
  * [`renderHeightmap`](#renderheightmap)
  * [`renderVectors`](#rendervectors)
  * [`levelGround`](#levelground)
  * [`renderBuildings`](#renderbuildings)
* [Tasks operating on heightmaps](#tasks-operating-on-heightmaps)
  * [`populateHeightmap`](#populateheightmap)
  * [`copyHeightmap`](#copyheightmap)

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

### `renderVectors`

Renders 2-D geometries (points, linear things and surfaces) with [placeables](Placeables.md) on a given [heightmap](Heightmaps.md).

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to render (required, models must be voxelizable in 2d)
- `heightmap`: [Heightmap](Heightmaps.md) to use (required).
- `place`: [Placeable](Placeables.md) to place on each voxel of shapes (optional)
- `inside`: [Placeable](Placeables.md) to place on each voxel inside shapes (optional)
- `borders`: [Placeable](Placeables.md) to place on each voxel of shapes borders (optional)

**NOTE**: `place` cannot be used with `inside` or `borders` (`inside` and `borders` may be used together).

#### Examples

Draw "building" shapes with cobble on ground:
```yaml
type: renderVectors
models:
  type: building
heightmap: ground
place: default:cobble
```

Draw same shapes but with wooden borders and glass inside:
```yaml
type: vector
models:
  type: building
heightmap: ground
borders: default:wood
inside: default:glass
```

### `levelGround`

Levels a heightmap under selected models.

If `heightmap` is not flat under a model, it will be risen enough to be flat under model surface. `filling` voxels will be added to world and `heightmap` will be updated accordingly.

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to render (required, models must be voxelizable in 2d)
- `heightmap`: Ground [heightmap](Heightmaps.md), should be a [stored heightmap](Heightmaps.md#stored-heightmap), will be updated according to leveling (required)
- `filling`: [Placeable](Placeables.md) placed beneath the model, ensuring it connects to the ground and does not appear to float.

#### Example

```yaml
type: levelGround
models:
  type: buildings
heightmap: ground
filling: default:cobble
```

### `renderBuildings`

Renders buildings using selected models as buildings footprint.

Building height is given by `height` metadata (the behavior of this task is undefined if value is missing, negative or zero).

#### Extra parameters

- `models`: [Selection of models](ModelSelection.md) to render (required, models must be voxelizable in 2d)
- `heightmap`: [Heightmap](Heightmaps.md) to use (required).
- `roof`: [Placeable](Placeables.md) used to render roofs.
- `wall`: [Placeable](Placeables.md) used to render walls.
- `window`: [Placeable](Placeables.md) used to render windows.

#### Required model metadata

- `height`: Height of building to render (must be a positive integer)

#### Example

```yaml
type: renderBuildings
models:
  type: buildings
heightmap: ground
roof: default:cobble
wall: default:brick
window: default:glass
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

# Renderers

A renderer places voxels in the world. It can also perform operations on [heightmaps](Heightmaps.md).
Each renderer has a field `type` which is used to identify it.

## Table of contents
* [Operating on world](#operation-on-world)
  * [Heightmap renderer](#heightmap-renderer)
  * [Vector renderer](#vector-renderer)
  * [Leveling renderer](#leveling-renderer)
  * [Building renderer](#building-renderer)
* [Operating on heightmaps](#operation-on-heightmaps)
  * [MatrixToHeightmap renderer](#matrixtoheightmap-renderer)
  * [CopyHeightmap renderer](#copyheightmap-renderer)

## Operating on world
### Heightmap renderer
This renderer places [placeables](Placeables.md) on one heightmap (`at`) or between two (`minimum` and `maximum`).

#### Type `heightmapRenderer`

#### Extra parameters
- `minimum`: The minimum [heightmap](Heightmaps.md) to use.
- `maximum`: The maximum [heightmap](Heightmaps.md) to use.
- `at`: The [heightmap](Heightmaps.md) to use.
- `place`: The [placeable](Placeables.md) to place at each heightmap voxel.

**NOTE**: `at` cannot be used with `minimum` or `maximum` (when `at` is absent, `minimum` and `maximum` must be used together).

#### Example
This will place grass voxels at the height of the specified heightmap. For better rendering, place voxel structure instead of a single voxel.

```yaml  
type: heightmapRenderer  
at: ground  
place: default:grass
```

This will place water voxels between the water and ground heightmaps.
```yaml  
type: heightmapRenderer  
minimum: water  
maximum: ground  
place: default:water  
```

### Vector renderer

This renderer draws 2-D geometries (points, linear things and surfaces) on a given heightmap.

#### Type `vector`

#### Extra parameters

- `models`: [Selection of models](GenerationParameters.md#model-selections) to render (required, models must be voxelizable in 2d)
- `heightmap`: [Heightmap](Heightmaps.md) to use (required).
- `place`: [Placeable](Placeables.md) to place on each voxel of shapes (optional)
- `inside`: [Placeable](Placeables.md) to place on each voxel inside shapes (optional)
- `borders`: [Placeable](Placeables.md) to place on each voxel of shapes borders (optional)

**NOTE**: `place` cannot be used with `inside` or `borders` (`inside` and `borders` may be used together).

#### Examples

Draw "building" shapes with cobble on ground:
```yaml
type: vector
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

### Leveling renderer

Levels ground under selected models.

Ground height is given by `heightmap`. If it's not flat under a model, it will be risen enough to be flat over model surface. `filling` voxels will be added to world and `heightmap` will be updated accordingly.

#### Type `leveling`

#### Extra parameters
- `models`: [Selection of models](GenerationParameters.md#model-selections) to render (required, models must be voxelizable in 2d)
- `heightmap`: Ground [heightmap](Heightmaps.md), should be a [stored heightmap](Heightmaps.md#stored-heightmap), will be updated according to leveling (required)
- `filling`: [Placeable](Placeables.md) placed beneath the model, ensuring it connects to the ground and does not appear to float.

#### Example

```yaml
type: leveling
models:
  type: buildings
heightmap: ground
filling: default:cobble
```

### Building renderer

Renders buildings using selected models as buildings footprint.

Building height is given by `height` metadata (the behavior of this renderer is undefined if value is missing, negative or zero).

#### Type `building`

#### Extra parameters

- `models`: [Selection of models](GenerationParameters.md#model-selections) to render (required, models must be voxelizable in 2d)
- `heightmap`: [Heightmap](Heightmaps.md) to use (required).
- `roof`: [Placeable](Placeables.md) used to render roofs.
- `wall`: [Placeable](Placeables.md) used to render walls.
- `window`: [Placeable](Placeables.md) used to render windows.

#### Required model metadata

- `height`: Height of building to render (must be a positive integer)

#### Example

```yaml
type: building
models:
  type: buildings
heightmap: ground
roof: default:cobble
wall: default:brick
window: default:glass
```

## Operating on heightmaps

### MatrixToHeightmap renderer

This renderer populates a heightmap with models data. Existing data is overwritten.

#### Type `matrixToHeightmap`

#### Extra parameters

- `models`: [Selection of models](GenerationParameters.md#model-selections) to render (required, models must be float matrices)
- `heightmap`: [Heightmap](Heightmaps.md) to populate, should be a [stored heightmap](Heightmaps.md#stored-heightmap) as it will be updated (required)

#### Example

```yaml
type: matrixToHeightmap
models:
  type: altitude
heightmap: ground
```

### CopyHeightmap Renderer
It copies the values of a heightmap to another at all coordinates within the model's shape.

##### Type `copyHeightmap`

##### Extra parameters
- `models`: [Selection of models](GenerationParameters.md#model-selections) to use as a filter  (required, models must be voxelizable in 2d).
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

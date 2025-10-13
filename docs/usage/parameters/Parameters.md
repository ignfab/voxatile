# Generation parameters

Generator makes a heavy use of parameters. Generation is described in Yaml (or Json).

## Table of contents

* [Example](#example)
* [Fields description](#fields-description)
* [References](#references)
* [Dependencies](#dependencies)

## See also

Only root Yaml parameters are described here. More specific aspects could be found in these other files:

* [FetchDataTask.md](FetchDataTask.md): Description of tasks for fetching data (see [TileTasks.md](TileTasks.md)).
* [Heightmaps.md](Heightmaps.md): Declaration and usage of heightmaps.
* [ModelSelection.md](ModelSelection.md): Selection of models (see [TileTasks.md](TileTasks.md)).
* [Placeables.md](Placeables.md): Description of things that can be placed in resulting world (see [TileTasks.md](TileTasks.md)).
* [TileTasks.md](TileTasks.md): Tasks that can be run on a generation tile.

## Example

Here is a basic generation parameters set that generates a map of 1000x1000 voxels around IGN building at Saint-Mandé (FR), in Minetest format, with 1 meter voxels in every direction:

```yaml
worldName: My World
references:
  - &voxel-surface default:cobble
  - &ground
    structure:
      - put: default:dirt_with_grass
        at: [ 0, 0, 0 ]
      - put: default:dirt
        at: [ 0, 0, -10..-1 ]
area:
  center:
    latitude: 48.845
    longitude: 2.425
  extentX: 1000
  extentY: 1000
  angle: 0
verticalScale: 1.0
horizontalScale: 1.0
crs: EPSG:2154
format: minetest
heightmaps:
  ground:
    default: 0
forEachTile:
  # Fetch data
  fetchAltitude:
    type: fetchData
    modelType: altitude
    provider:
      type: wmsFloat
      url: https://data.geopf.fr/wms-r/wms
      layer: RGEALTI-MNT_PYR-ZIP_FXX_LAMB93_WMS
    processor:
      type: floatMatrix
  fetchBuildings:
    type: fetchData
    modelType: building
    provider:
      type: wfs
      url: https://data.geopf.fr/wfs/wfs
      features: BDTOPO_V3:batiment
    processor:
      type: geoToolsVector
  # Copy altitude to ground heightmap
  setAltitude:
    after:
      - fetchAltitude
    type: populateHeightmap
    models:
      type: altitude
    heightmap: ground
  # Rendering
  renderGround:
    after:
      - setAltitude
    type: renderHeightmap
    at: ground
    place: *ground
  renderBuildings:
    after:
      - fetchBuildings
      - renderGround
    type: renderSurfaces
    models:
      type: building
    heightmap: ground
    place: *voxel-surface
```

## Fields description

- `worldName`: World name (text, default `Minalac`)
- `references`: Ignored field where references (or other content) can be put in
- `area`: Area to be rendered
  - `center`: Coordinates of the area's center point, expressed in the commonly used coordinate system (EPSG:4326)
    - `latitude`: latitude in decimal degrees
    - `longitude`: longitude in decimal degrees
  - `extentX` (int): Horizontal extent of the area along the x-axis (in voxels)
  - `extentY` (int):  Horizontal extent of the area along the y-axis (in voxels)
  - `angle` (float): Clockwise rotation angle around area center (in degrees, optional, default 0)
- `verticalScale`: Vertical size of voxels (in map units, usually meters)
- `horizontalScale`: Horizontal size of voxels (in map units, usually meters)
- `crs`: Coordinate Reference System to be used for projecting geographical data into voxel world
- `format`: Output format ([`luanti`](Luanti.md) or [`minecraft`](Minecraft.md))
- `heightmaps`: [Heightmaps](Heightmaps.md) used for the generation
  - _`<name>`_: Unique name of the heightmap
      - `default`: Default value for all heightmap cells (integer or one of `minimal`, `min`, `maximal` or `max`)
- `forEachTile`: [Tile tasks](TileTasks.md) to perform for each generation tile
  - _`<name>`_: Unique name of the task
    - `after`: List of [dependencies](#dependencies) (optional)
    - `type` (str): Tile task [type](TileTasks.md)
    - Additional parameters specific to the given [type](TileTasks.md)
- `afterAllTiles`: [Tasks](Tasks.md) to perform after all generation tiles finished
  - _`<name>`_: Unique name of the task
    - `after`: List of [dependencies](#dependencies) (optional)
    - `type` (str): Task [type](Tasks.md)
    - Additional parameters specific to the given [type](Tasks.md)

## References

YAML references are processed by the generator. They can be defined anywhere in parameters. A `references` field is available at root to put references not going anywhere else.

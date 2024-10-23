# Generation parameters

Generation parameters can be written in Yaml or Json. Yaml is better for readability and Json for communication between programs.

These parameters generate a map of 1000x1000 voxels around IGN building at Saint-Mandé (FR), in Minetest format, with 1 meter voxels in every direction:

```yaml
heightmaps:
  ground:
    default: 0
sources:
  bati:
    modelType: building
    provider:
      type: wfs
      url: https://data.geopf.fr/wfs/wfs
      features: BDTOPO_V3:batiment
    processor:
      type: geoToolsVector
    postProcessors:
      - type: copy
        metadata: hauteur
        to: height
renderers:
  building:
    after:
      - source:bati
    type: vector
    modelType: building
    heightmap: ground
    inside: COBBLE
    edge: STONE
area:
  center:
    latitude: 48.845
    longitude: 2.425
  extendX: 1000
  extendY: 1000
verticalScale: 1.0
horizontalScale: 1.0
crs: EPSG:2154
format: minetest
```

## Fields description

- `heightmaps`: Heightmaps available for the generation
  - _`<name>`_: Unique name of the heightmap
      - `default` (int or str): Default value for all heightmap cells (integer or one of `minimal`, `min`, `maximal` or `max`)
- `sources`: Sources providing models to render
  - _`<name>`_: Unique name of the source
    - `after` (optional list): List of [dependencies](#dependencies)
    - `modelType`: the type of models to create
    - `provider`: Definition of the data provider to use
      - `type`: Type of data provider
      - Additional parameters specific to the given [type](#provider-parameters)
    - `processor`: Definition of the data processor to use
      - `type`: Type of data processor
      - Additional parameters specific to the given [type](#processor-parameters)
    - `postProcessors`: Definition of post processors to use
      - _List item_:
        - `type`: Type of post processor
        - Additional parameters specific to the given [type](#post-processor-parameters)
- `renderers`: Renderers used to generate the map
  - _`<name>`_: Unique name of the renderer
    - `after` (list): List of [dependencies](#dependencies)
    - `type` (str): Type of the renderer
    - Additional parameters specific to the given [type](#renderer-parameters)
- `area`: Area to be rendered
  - `center`: Contains the coordinates of the area's center point, expressed in the commonly used coordinate system (EPSG:4326)
    - `latitude`: latitude in decimal degrees
    - `longitude`: longitude in decimal degrees
  - `extendX` (int): Horizontal extend of the area along the x-axis (in voxels)
  - `extendY` (int):  Horizontal extend of the area along the y-axis (in voxels)
- `verticalScale`: Vertical size of voxels (in map units, usually meters)
- `horizontalScale`: Horizontal size of voxels (in map units, usually meters)
- `crs`: Coordinate Reference System to be used for projecting geographical data into voxel world
- `format`: Output format (`minetest` or `minecraft`)

## Dependencies

Dependencies allow to manage execution order of sources and renderers. Optional `after` fields contains a list of every source or renderer that should run before the source or renderer it belongs to.

In `after` list, `source:<name>` refers to sources and `renderer:<name>` refers to renderers (`<name>` being the unique name given to source or renderer in its definition).

For example, if we want `buildings` renderer to run after `buildings` source and `altitude` renderer, we write:
```yaml
renderers:
  buildings:
    after:
      - source:buildings
      - renderer:altitude
    ...
```

**BEWARE** : For now, `after` values are not checked. Generator will get stuck waiting in case of dependency loop or if a value refers to nonexistent source or renderer.

## Provider parameters

A provider fetches data from a source and provides it as-is to a processor. Provider type is identified by `type` field.

### Web Feature Service
A provider capable of fetching vector data from a [Web Feature Service](https://en.wikipedia.org/wiki/Web_Feature_Service) source. Source must support WFS 1.1 and GML 3.1 versions.

**Type**: `wfs`

**Extra parameters**:
- `url` (required): Base URL, including protocol and path, excluding request arguments (example: `https://data.geopf.fr/wfs/wfs`)
- `features` (required): Name of WFS feature type to fetch
- `crs` (optional): Wanted CRS for these features (defaults to target CRS)

**Suitable processors**: `geoToolsVector`

### Web Map Service with floating point values
A provider capable of fetching **float** data from a [Web Map Service](https://en.wikipedia.org/wiki/Web_Map_Service) source. Source must be able to provide `x-bil` format.

**Type**: `wmsFloat`

**Extra parameters**:
- `url` (required): Base URL, including protocol and path, excluding request arguments (example: `https://data.geopf.fr/wms-r/wms`)
- `layer` (required): Name of layer to fetch
- `crs` (optional): Wanted CRS for this layer (defaults to target CRS)

**Suitable processors**: `floatMatrix`

## Processor parameters

A processor converts data from a provider into models. Processor type is identified by `type` field.

### GeoTools vector processor

This processor is able to take vector features and turn them into models, using GeoTools library.

**Type**: `geoToolsVector`

**Extra parameters**: None

**Suitable providers**: `wfs`

### Float matrix vector processor

Processor translating a float data matrix to a model.

**type**: `floatMatrix`

**Extra parameters**: None

**Suitable providers**: `wmsFloat`

## Post processor parameters

A post-processor alters models so they can comply with renderers requirements.

### Metadata copy

A post-processor copying a metadata into another.

**Type**: copy

**Extra parameters**:
- `metadata` (required, `text`): Name of the metadata to copy.
- `to` (required, `text`): Name of destination metadata.
- `abortIfMetadataIsAbsent` (optional, default `no`): `yes` to stop the copy if the metadata is missing, `no` to allow the copy to proceed even if the metadata is missing (in this case, the metadata value will be empty).
- `keepExisting` (optional, default `no`): `no` to overwrite existing data, `yes` to keep existing metadata.

## Renderer parameters

Each renderer has a field `type` which is used to identify it.

### Ground renderer

This renderer places a column of voxels under a heightmap.

**Type**: ground

**Extra parameters**:
- `heightmap`: Name of the heightmap to use
- `place`: [Placeable](#placeables) to place at each heightmap voxel.

Example:
```yaml
type: ground
heightmap: altitude
place: default:grass
```
This will put a grass voxel at heightmap altitude. For better rendering, place voxel structure instead of a single voxel.

### Vector renderer

```yaml
type: vector
modelType: building
heightmap: ground
inside: default:coble
edge: default:stone
```

Fields:
- `type`: Must be the value `vector`.
- `modelType`: the type of models to render
- `heightmap`: the name of the ground heightmap to use. It must exist.
- `inside`: [Placeable](#placeables) to place on each inside voxel
- `edge`: [Placeable](#placeables) to place on each edge voxel

### Heightmap renderer

```yaml
type: heightmap
modelType: mnt
heightmap: ground
```

Fields:
- `type`: Must be the value `heightmap`
- `modelType`: the type of models to render
- `heightmap`: the name of the heightmap to use. It must exist.

## Placeables

A placeable is something that can be placed in voxel world at a given position: a voxel, a structure or a pattern.

### Voxels

Voxel descriptions depend on chosen game format. In both Minecraft and Minetest, voxels are mainly defined by a string (block type or node type). They may have some extra optional parameters (like metadata).

When a string is given as value for a placeable field, it is interpreted as block type for Minecraft and node type for Minetest. All other parameters are set to default (see Minecraft and Minetest sections below). Other future formats may accept or not this shortcut.

Example of a simple voxel value for `place` field:
```yaml
place: default:stone # Stone node in Minetest
```

When parameters others than block/node type has to be provided, voxel description must be an object with fields. These fields depends on the game format.

#### Minecraft voxel description

Example of a Minecraft voxel description for `place` field:

```yaml
place:
  block: oak_leaves
  properties:
    persistent: true
```

Fields:
  - `block`: Block name in Minecraft (required)
  - `properties`: Block properties (optional, default none)
    - _`<property name>`_: Property value (refer to [Minecraft Wiki](https://minecraft.wiki/w/Block_states) for more information)

#### Minetest voxel description

Example of a Minetest voxel description for `place` field:

```yaml
place:
  node: stairs:stair_stone
  param2: 2
```

Fields:
  - `node`: Node type name in Minetest (required)
  - `param1`: Node param1 (integer, optional, default `0`)
  - `param2`: Node param2 (integer, optional, default `0`)

`param1` and `param2` meaning depends on node type. Refer to Minetest documentation for more information.

### Structures and patterns

Placeable may be structure or patterns. In that case, a `type` field indicates what structure or pattern to use.

This example shows usage of a `mystruct` structure as placeable:
```yaml
place:
  type: mystruct
  ... (extra fields for mystruct)
```

Note that `voxel` type corresponds to Minecraft/Minetest voxel description (see above).

See corresponding structure or pattern for extra fields.

#### Stack

**Type**: `stack`

Places a stack of placeable below or over the reference position. Stack is made of several different placeables repeated a given number of times.

Example of a stack structure as value to `place` field:
```yaml
place:
  type: stack
  direction: downwards
  layers:
    - material: default:dirt_with_grass
    - material: default:dirt
      height: 3
    - material: default:stone
      height: 10
```

Fields:
  - `direction`: `upwards` or `downwards` (optional, default `upwards`)
  - `layers`: A list of layers
    - _List item_:
      - `material`: A placeable, usualy a voxel (required)
      - `height`: How many times to repeat that placeable (optional, default `1`)

An upwards stack starts at reference position and stacks placeable upwards. A downards stack proceeds same but downwards.


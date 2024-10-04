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
    - `after` (optional list): List of [dependancies](#dependancies)
    - `modelType`: the type of models to create
    - `provider`: Definition of the data provider to use
        - `type`: Type of data provider
        - Additional parameters specific to the given [type](#provider-parameters)
    - `processor`: Definition of the data processor to use
        - `type`: Type of data processor
        - Additional parameters specific to the given [type](#processor-parameters)
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

**BEWARE** : For now, `after` values are not checked. Generator will get stuck waiting in case of dependency loop or if a value refers to nonexistant source or renderer.

## Providers parameters

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

## Renderer parameters

Each renderer has a field `type` which is used to identify it.


### Ground renderer

This renderer places a column of voxels under a heightmap.

**Type**: ground

**Extra parameters**:
- `heightmap`: Name of the heightmap to use
- `voxels`: Voxels to place as a pair of semantic type and thickness.

Example:
```yaml
type: ground
heightmap: altitude
voxels:
  GRASS: 1
  DIRT: 3
  STONE: 10
```
This will put a grass voxel at heightmap altitude, three dirt voxels underneath and then ten stone voxels (fourteen voxels in total under the heightmap's level).

### Vector renderer

```yaml
type: vector
modelType: building
heightmap: ground
inside: COBBLE
edge: STONE
```

Fields:
- `type`: Must be the value `vector`.
- `modelType`: the type of models to render
- `heightmap`: the name of the ground heightmap to use. It must exist.
- `inside`: the semantic type of the voxel used for the inside
- `edge`: the semantic type of the voxel used for the edges

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

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
    models:
      type: building
    heightmap: ground
    inside: default:cobble
    borders: default:stone
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
  - `extentX` (int): Horizontal extent of the area along the x-axis (in voxels)
  - `extentY` (int):  Horizontal extent of the area along the y-axis (in voxels)
  - `angle` (float): Clockwise rotation angle around area center (in degrees, optional, default 0)
- `verticalScale`: Vertical size of voxels (in map units, usually meters)
- `horizontalScale`: Horizontal size of voxels (in map units, usually meters)
- `crs`: Coordinate Reference System to be used for projecting geographical data into voxel world
- `format`: Output format (`minetest` or `minecraft`)
- `references`: Ignored field where references (or other content) can be put in

## References

YAML references are processed by the generator. They can be defined anywhere in parameters. A `references` field is available at root to put references not going anywhere else.

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
- `maxFeaturesPerQuery` (optional): Maximum number of features fetched per query (default 1000)

**Suitable processors**: `geoToolsVector`

### GeoPackage
A provider capable of reading vector data from a [GeoPackage](https://en.wikipedia.org/wiki/GeoPackage) file.

**Type**: `gpkg`

**Extra parameters**:
- `filePath` (required): Path of the GPKG file (absolute, or relative to execution context)
- `typeName` (required): Name of feature type to read
- `crsOverride` (optional, default none): CRS to use when reading data. By default, the CRS is read from the GeoPackage itself. You should only use this parameter if the CRS is invalid or missing from the file. This **DOES NOT** reproject data!

**Suitable processors**: `geoToolsVector`

### Shapefile
A provider capable of reading vector data from a [Shapefile](https://en.wikipedia.org/wiki/Shapefile).

**Type**: `shapefile`

**Extra parameters**:
- `filePath` (required): Path of the Shapefile (absolute, or relative to execution context)
- `crsOverride` (optional, default none): CRS to use when reading data. By default, the CRS is read from the Shapefile itself. You should only use this parameter if the CRS is invalid or missing from the file. This **DOES NOT** reproject data!

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

**Suitable providers**: `wfs`, `gpkg`, `shapefile`

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

### Metadata parse

Post-processor parsing a metadata value in-place.

**Type**: `parse`

**Extra parameters**:
- `metadata` (required): the name of the metadata to parse.
- `as` (required): the type of parsed value: `integer`, `decimal`, `boolean`, `text`.
- `ifMissing` (optional, default `error`): What to do when metadata is absent. See failure policies below.
- `ifNotParsable` (optional, default `error`): What to do if data parsing fails. See failure policies below.

| Failure policy | Explanation |
| :--- | :--- |
| `discardModel` | The model is discarded. |
| `removeMetadata` | The metadata is removed. |
| `ignore` | Failure is ignored, nothing is done. |
| `error` | An error occurs, and the generation stops. |

### Metadata default

Post-processor that applies a default value for a specified metadata.

**Type**: `default`

**Extra parameters**
- `metadata` (required): the name of the metadata.
- `value` (required): the default value to use if the metadata is not present.
- `as` (requires): the type to which the value should be converted:  `integer`, `decimal`, `boolean`, `text`

## Renderer parameters

Each renderer has a field `type` which is used to identify it.

### Ground renderer

This renderer places a column of voxels under a heightmap.

#### Type `ground`

#### Extra parameters

- `heightmap`: Name of the heightmap to use
- `place`: [Placeable](Placeables.md) to place at each heightmap voxel.

#### Example

```yaml
type: ground
heightmap: altitude
place: default:grass
```
This will put a grass voxel at heightmap altitude. For better rendering, place voxel structure instead of a single voxel.

### Vector renderer

This renderer draws 2-D geometries (points, linear things and surfaces) on a given heightmap.

#### Type `vector`

#### Extra parameters

- `models`: [Selection of models](#model-selections) to render (required, models must be voxelizable in 2d)
- `heightmap`: Name of the ground heightmap to use (required)
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


### Heightmap renderer

This renderer populates a heightmap with models data. Existing data is overwritten.

#### Type `heightmap`

#### Extra parameters

- `models`: [Selection of models](#model-selections) to render (required, models must be float matrices)
- `heightmap`: Name of the heightmap to populate (required, updated)

#### Example

```yaml
type: heightmap
models:
  type: altitude
heightmap: ground
```

### Leveling renderer

Levels ground under selected models.

Ground height is given by `heightmap`. If it's not flat under a model, it will be risen enough to be flat over model surface. `filling` voxels will be added to world and `heightmap` will be updated accordingly.

#### Type `leveling`

#### Extra parameters
- `models`: [Selection of models](#model-selections) to render (required, models must be voxelizable in 2d)
- `heightmap`: Name of the ground heightmap (required, updated according to leveling)
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

- `models`: [Selection of models](#model-selections) to render (required, models must be voxelizable in 2d)
- `heightmap`: Name of the heightmap to use (required)
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

## Model selections

A model selection describes characteristics that models must match to be processed.

Models are always selected on their type and that selection may be narrowed down by an extra filter. This filter can be a combination of other filters.

Example of a model selection:
```yaml
models:
  type: buildings
  filter:
    and:
      - hasMetadata: height
      - not:
          metadata: classification
          in: [ Monument, Castle, Chapel, Church ]
```

Fields:
  - `type` (required): Type of models to select
  - `filter` (optional, default none): Extra filter


### Boolean operations

Usual boolean operations can be applied on filters to alter them or combine them.

#### Negation

A filter can be negated using `not` filter.

Example:
```yaml
  not:
    ...filter...
```

Fields:
  - `not`: The filter to negate (required)

#### "And" combination

Several filters can be combined using `and` filter. Result is true only if all of combined filters are true.

Example:
```yaml
  and:
    - ...filter1...
    - ...filter2...
    - ...filter3...
```

Fields:
  - `and` (required): A list of filters

#### "Or" combination

Several filters can be combined using `or` filter. Result is true if at least one of combined filters is true.

Example:
```yaml
  or:
    - ...filter1...
    - ...filter2...
    - ...filter3...
```

Fields:
  - `or` (required): A list of filters

#### Combination

Of course, `and`, `or` and `not` can be combined in complex expressions, for example:
```Yaml
and:
  - ...filter1...
  - not:
      ...filter2...
  - or:
    - ...filter3...
    - ...filter4...
```
This is equivalent to *filter1 AND (NOT filter2) AND (filter3 OR filter4)*.

### Filtering on metadata

These filters rely on models metadata.

#### Has

Returns true if given metadata exists for the model.

Examples:
```yaml
   hasMetadata: classification
```

```yaml
   hasMetadata: [ classification, height ]
```

Fields:
  - `hasMetadata` (required): Metadata name or list of metadata names to check

If a list of metadata name is given, model is selected only if it has all given metadata.

#### Equals

Returns true if given metadata has the given value.

Example:
```yaml
   metadata: height
   equals: 2
   as: integer
```

Fields:
  - `metadata` (required): Name of metadata to check
  - `equals` (required): Value to compare with
  - `as` (optional, default `text`): Type of value to compare with (`integer`, `decimal`, `text` or `boolean`)

**Be careful with typing!** Value comparison includes type. Values with different type won't be equal. Metadata with text value "5" won't equal to 5 as integer.

#### In

Returns true if given metadata value is among given values.

Example:
```yaml
   metadata: classification
   in: [ Monument, Castle, Chapel, Church ]
   as: text
```

Fields:
  - `metadata` (required): Name of metadata to check
  - `in` (required): List of values to compare with
  - `as` (optional, default `text`): Type of values to compare with (`integer`, `decimal`, `text` or `boolean`)

#### Lower Than

Selects model with given metadata value is strictly less than the specified threshold.

Example:
```yaml
   metadata: height
   lowerThan: 20
```

Fields:
  - `metadata` (required): Name of the metadata to check
  - `lowerThan` (required): Threshold value that the metadata must be lower than. (Only possible to compare numbers with this filter)

#### Greater Than

Selects the model with the given metadata value that is strictly greater than the specified threshold.

Example:
```yaml
   metadata: height
   greaterThan: 20
```

Fields:
  - `metadata` (required): Name of the metadata to check
  - `greaterThan` (required): Threshold value that the metadata must be greater than. (Only possible to compare numbers with this filter)

# Generation parameters

Generation parameters can be written in Yaml or Json. Yaml is better for readability and Json for communication between programs.

These parameters generate a map of 1000x1000 voxels around IGN building at Saint-Mandé (FR), in Minetest format, with 1 meter voxels in every direction:

```yaml
heightmaps:
  ground:
    default: 0
renderers:
  building:
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

Available fields:
- `heightmaps`: Heightmaps available for the generation
  - _`<name>`_: Name of the heightmap
      - `default` (int or str): Default value for all heightmap cells (integer or one of `minimal`, `min`, `maximal` or `max`)
- `renderers`: Renderers used to generate the map
  - _`<name>`_: Name of the renderer
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

# Renderer parameters

Each renderer has a field `type` which is used to identify it.

## Vector renderer

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

## Heightmap Renderer

```yaml
type: heightmap
modelType: mnt
heightmap: ground
```

Fields:
- `type`: Must be the value `heightmap`
- `modelType`: the type of models to render
- `heightmap`: the name of the heightmap to use. It must exist.

# Generation parameters

Generation parameters can be written in Yaml or Json. Yaml is better for readability and Json for communication between programs.

These parameters generate a map of 1000x1000 voxels around IGN building at Saint-Mandé (FR), in Minetest format, with 1 meter voxels in every direction:

```yaml
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
 - `area`: Area to be rendered
   - `center`: `longitude` and `latitude` of the center point
   - `extendsX` and `extendsY`: Area extends in both horizontal directions (in voxels)
 - `verticalScale`: Vertical size of voxels (in map units, usually meters)
 - `horizontalScale`: Horizontal size of voxels (in map units, usually meters)
 - `crs`: Coordinate Reference System to be used for projecting geographical data into voxel world
 - `format`: Output format (`minetest` or `minecraft`)

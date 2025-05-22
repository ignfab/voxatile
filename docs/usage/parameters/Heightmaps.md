
# Heightmap

A heightmap is a map, in voxel units, where each cell contains a certain value. It is typically used to contain elevation: it can be used to store ground height or water depth.
Some heightmaps can be used for intermediate calculations.

## Table of contents

* [Stored heightmap](#stored-heightmap)
* [Readonly heightmap](#readonly-heightmap)
  * [Constant heightmap](#constant-heightmap)
  * [Local minimum heightmap](#local-minimum-heightmap)
  * [Manhattan heightmap](#manhattan-heightmap)
  * [Remap heightmap](#remap-heightmap)
  * [Sum heightmap](#sum-heightmap)
  * [Product heightmap](#product-heightmap)

## Stored heightmap

Stored heightmaps are read/write heightmaps accessible throughout the generation by their name.
These heightmaps are declared in the root [parameters file](Parameters.md) under the `heightmaps` field.
On the example below, a heightmap named `ground` with a default value of 5 is declared.

```yaml
heightmaps:
  ground:
    default: 5
```
Fields:
- `default`: The default value for all heightmap cells (integer or one of `minimal`, `min`, `maximal` or `max`)

## Readonly heightmap

Throughout the generation, readonly heightmaps can be created.
This can be handy, for example, when we need a heightmap for a specific purpose without storing it.

### Constant heightmap

Whenever a heightmap is required, you can provide a constant heightmap that returns the same value for every point.
There are two ways to do it: one is by simply providing the value and the other is by using the field `constant`.
On the example below, `minimum` and `maximum` are both expecting a heightmap. The first syntax was used for `minimum`, and the second was used for `maximum`.
```yaml
ground:
  type: renderHeightmap
  minimum: 2
  maximum:
    constant: 5
  place: *voxel-grass
```
Field:
- `constant`: the value of the heightmap (Required)
### Local minimum heightmap

The local minimum heightmap is a heightmap that gives the local minimum, around a specified range (at (x ± range; y ± range)), at each point of the specified heightmap.
```yaml
localMin: ground
range: 9
```

Fields:
- `localMin`: the base heightmap (Required)
- `range`: the range (Required, must be positive)

### Manhattan heightmap

This heightmap takes a heightmap and transforms its values to Manhattan distances.
The distance is a capped Manhattan distance to the nearest point on the provided heightmap matching the specified `targetValue`.
The value is capped at `maximumDistance`

```yaml
manhattan: water
maximumDistance: 12
targetValue: 0
```

Fields:
- `manhattan`: the base heightmap (Required)
- `maximumDistance`: the maximum allowed distance (Optional. Default value: 10)
- `targetValue`: the value used for distance calculations. (Optional. Default value: 0)

### Remap heightmap
This heightmap remaps the values of an existing heightmap according to a predefined interval mapping.

The new value is the value associated with the first interval containing the original value.

On the example below, wherever the ground is equal to 0, the new heightmap will be 21, and when the ground is between -1 and 2 (inclusive), the new heightmap will be 34. Other values will be untouched, meaning for example whenever the ground is 4, the new heightmap will remain 4 (no match, so the original value is used).
```yaml
remap: ground
mapping:
  0: 21
  -1..2: 34
```

Fields:
- `remap`: the base heightmap (Required)
- `mapping`: the mapping associating intervals with values (Required)

### Sum heightmap

The sum heightmap consists of a sum of heightmaps.

```yaml
sum:
  - ground
  - 5
```

Fields:
- `sum`: the list of heightmaps to be added together.
### Product heightmap

The product heightmap consists of a product of heightmaps.

```yaml
product:
  - ground
  - -1
```

Fields:
- `product`: the list of heightmaps to be multiplied together.

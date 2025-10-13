# Luanti output format

Luanti output format exports world in Luanti map version 28, convertible in-game to newer versions.

## Table of contents

* [Output format description](#output-format-description)
* [Voxel placeable description](#voxel-placeable-description)
  * [Full format](#full-format)
  * [Short format](#short-format)

## Output format description

```yaml
format: luanti
```

## Voxel placeable description

Voxels in Luanti are [nodes](https://docs.luanti.org/for-players/nodes/). Refer to the documentation of the selected game (e.g. [Minetest Game](https://wiki.minetest.org/Games/Minetest_Game/Nodes)) and mods to find a list of possible values.

#### Full format

Example of a Luanti voxel description for `place` field:

```yaml
place:
  node: stairs:stair_stone
  param2: 2
```

Fields:
- `node`: Node type name in Luanti (required)
- `param1`: Node param1 (integer, optional, default `0`)
- `param2`: Node param2 (integer, optional, default `0`)

`param1` and `param2` meaning depends on node type. Refer to game documentation for more information.

#### Short format

The Luanti voxel short form string is interpreted as the node type, with all other optional parameters set to their default value.

In other words, writing:
```yaml
place: default:stone
```
is strictly equivalent to:
```yaml
place:
  node: default:stone
  param1: 0
  param2: 0
```

This short form thus doesn't support setting `param1` or `param2` values.

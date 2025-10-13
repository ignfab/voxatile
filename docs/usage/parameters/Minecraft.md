# Minecraft output format

Minecraft output format exports world in Minecraft Java Edition 1.21.11, convertible in-game to newer versions.

## Table of contents

* [Output format description](#output-format-description)
* [Voxel placeable description](#voxel-placeable-description)
  * [Full format](#full-format)
  * [Short format](#short-format)

## Output format description

```yaml
format: minecraft
```

## Voxel placeable description

Voxels in Minecraft are [blocks](https://minecraft.wiki/w/Block#List_of_blocks).

### Full format

Example of a Minecraft voxel description for `place` field:

```yaml
place:
  block: oak_leaves
  properties:
    persistent: true
```

Example of a Minecraft block entity voxel description for `place` field:

```yaml
place:
  block: white_banner
  properties:
    rotation: 8
  blockEntity: banner
  dataTags: >-
    {
      patterns: [
        {
          color: "blue",
          pattern: "stripe_left"
        },
        {
          color: "red",
          pattern: "stripe_right"
        }
      ]
    }
```

Fields:
- `block`: Block name in Minecraft (required, will be namespaced using `minecraft:` if no namespace given)
- `properties`: Block properties (optional, default none)
  - _`<property name>`_: Property value (refer to [Minecraft Wiki](https://minecraft.wiki/w/Block_states) for more information)

Additional fields applicable only for [block entities](https://minecraft.wiki/w/Block_entity):
- `blockEntity`: Block entity ID in Minecraft, or `true` to use the same value as the `block` field (required, will be namespaced using `minecraft:` if no namespace given)
- `dataTags`: Block entity data encoded as [SNBT](https://minecraft.wiki/w/NBT_format#SNBT_format) (optional, default none, refer to [Minecraft Wiki](https://minecraft.wiki/w/Block_entity_format#Types) for more information)

### Short format

The Minecraft voxel short form string is interpreted as a compact block definition, including block type, optional properties and optional block entity data.
It follows the syntax of the [block state command argument type](https://minecraft.wiki/w/Argument_types#minecraft:block_state): `block_name[property=value,other_property=other_value]{DataTags: "NBT data"}`.

When data tags are absent (no `{}`), it is a simple block with properties.

In other words, writing:
```yaml
place: stone
```
is strictly equivalent to:
```yaml
place:
  block: minecraft:stone
  properties: {}
```

Another example, with properties:
```yaml
place: blue_candle[candles=3,lit=true]
```
equivalent to:
```yaml
place:
  block: minecraft:blue_candle
  properties:
    candles: 3
    lit: true
```

When data tags are provided, the block is turned into a block entity, using the block type as the block entity ID.

In other words, writing:
```yaml
# Value is quoted here to prevent YAML interpretation of data tags
place: 'jukebox[has_record=true]{RecordItem: {id: "minecraft:music_disc_cat"}, ticks_since_song_started: 0L}'
```
is strictly equivalent to:
```yaml
place:
  block: default:jukebox
  properties:
    has_record: true
  blockEntity: true
  dataTags: '{RecordItem: {id: "minecraft:music_disc_cat"}, ticks_since_song_started: 0L}'
```

This short form thus doesn't support setting `blockEntity` value to something different from the `block`.

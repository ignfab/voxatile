# Generator cookbook

Welcome to the generator voxel kitchen.
Here you can learn the best eipes for cooking your voxel soup.

Recipes are mostly based on OSM data and Luanti output format for Minetest Game but they can be easily transposed to other sources and formats.

In Luanti, voxel are named after this pattern: `modname:nodename`, `modname` being the mod name (`default` for most of Minetest Game voxels) and `nodename` is the voxel name.

We use those voxels:
- `default:stone`
- `default:dirt`
- `default:dirt_with_grass`

In OSM, objects (buildings, roads, ...) are distiguised by tags. Knowledge of tags is important to know what to get from OSM.

## Reminder

All recipes consists in writing a parameter file. This file is written in [Yaml](https://yaml.org/) format (it can also be written in [Json](https://json.org/)).

Once written, launch generation:

```shell
java -jar Generator.jar -p params.yaml world/output
```

* `Generator.jar`: path to the generator jar file (depends how it has been build).
* `params.yaml`: path to the parameter file.
* `world/output`: path to output directory.

Output directory should be empty or non existent. Delete it (or change path) before launching another generation.

For more detailed documentation, refer to [Generator.md](../usage/Generator.md).

## How to start?

First, read the [How to start from scratch](scratch.md).

Once done, you can read the other howtos in any order unless specified:

* [How to deal with ground](ground.md)
* [How to render water](water.md)
* [How to draw roads](roads.md)
* [How to render simple buildings](simple-buildings.md)

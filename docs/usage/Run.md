# Run

> [!IMPORTANT]
> Don't forget to configure your [proxy](../Proxy.md) if needed!

## TL;DR;

Compile and run from repository root:

```shell
mvn -Dmaven.test.skip=true clean package && ./generate.sh minetest full ign $HOME/.minetest/worlds/minalac
```

Run only (from repository root):

```shell
./generate.sh minetest full ign $HOME/.minetest/worlds/minalac
```

## generate.sh

`generate.sh` scripts creates yaml configuration from configuration fragments and passes it to `Generator.jar`.

It mimics a simple behavior of future `minalac-configurator` and is only intended to be used for testing purpose.

Usage:
```bash
generate.sh [options] <format> <process> <place> [<outputPath>]
```

Where:
- `options` may be:
   - `-y` do nothing but displaying resulting parameters (in Yaml format).
   - `-g` do not perform generation.
   - `-s` do not save generated map to disk.
- `<format>` is the wanted generation format, refering to yaml file (without extension) in `examples/formats`.
- `<process>` is the wanted generation process, refering to yaml file (without extension) in `examples/processes`.
- `<places>` is the wanted generation area, refering to yaml file (without extension) in `examples/places`.
- `<outputPath>` is where world files will be generated, only required if no option set (if directory exists, it will be emptied).

While many places are available, there are only two formats (`minecraft`, `minetest`) and one process (`full`) for now.

To perform tiled generation, set `MINALAC_MAX_TILE_SIZE` environment variable to the wanted maximum tile size:

```shell
MINALAC_MAX_TILE_SIZE=512 ./generate.sh minetest full ign $HOME/.minetest/worlds/minalac
```

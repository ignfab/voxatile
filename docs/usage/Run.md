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

## generate.sh (Unix)

`generate.sh` scripts creates yaml configuration from configuration fragments and passes it to `Generator.jar`.

It mimics a simple behavior of future `minalac-configurator` and is only intended to be used for testing purpose.

Usage:
```bash
generate.sh [options] <format> <process> <place> [<outputPath>]
```

Where:
- `options` may be:
   - `-h` do nothing but displaying the help message.
   - `-g` do not perform generation.
   - `-s` do not save generated map to disk.
  - `-y` do nothing but displaying resulting parameters (in Yaml format).
  - `-l (formats|processes|places)` do nothing but listing available formats/processes/places.
- `<format>` is the wanted generation format, referring to yaml file (without extension) in `examples/formats`.
- `<process>` is the wanted generation process, referring to yaml file (without extension) in `examples/processes`.
- `<places>` is the wanted generation area, referring to yaml file (without extension) in `examples/places`.
- `<outputPath>` is where world files will be generated, only required if no option set (if directory exists, it will be emptied).

While many places are available, there are only two formats (`minecraft`, `minetest`) and one process (`full`) for now.

## Windows

On Windows, the same script is available as `generate.bat` and `generate.ps1`, respectively as batch and powershell scripts.

For the `generate.bat` version, options are prefixed by `/` instead of `-`. For help, use `generate.bat /h`.
Note : Due to technical limitation, this script relies on a temporary file to combine Yaml parameters. By default, that file will be in the `examples` directory and named `temp.yaml`. If, for some reason, a file named like that already exists, the script will abort to prevent any undesired overwriting. You can change that name at the beginning of the script, in the variables' setup.

For the `generate.ps1` version, options have both long and short names. For help, use `.\generate.ps1 -help`.

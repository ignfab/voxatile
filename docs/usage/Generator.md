# Run

> [!IMPORTANT]
> Don't forget to configure your [proxy](../Proxy.md) if needed!

This page is about different methods for building and running Generator. They are provided for informational purpose. If you just want to run the generator, please refer to [Run.md](Run.md).

## Generation parameters

Generation involves too many parameters to be passed on command line. They are passed either by file using `-p`/`--param-file` command line option or using `VOXATILE_PARAMS` environment variable.

Refer to [generation parameters documentation](parameters/Parameters.md) for further information.

To avoid useless efforts building a parameter file, you can use provided `generate.sh` script (see [Run.md](Run.md#generatesh)).

## Command line arguments
Usage:
```
java -jar Generator.jar [OPTIONS] <outputPath>
```

Options:
- `-h` or `--help` display command usage.
- `-p <path>` or `--param-file <path>` read generation parameters from given path. If omitted, parameters are read from [`VOXATILE_PARAMS`](#voxatile_params) environment variable.
- `--generation-disabled` Stop before starting generation, after parameters parsed.
- `--save-disabled` Stop before saving output file, after generation done.
- `--max-tile-size <size>` Perform tiled generation with tiles no larger than `size` (positive integer) in both dimensions. See also [`VOXATILE_MAX_TILE_SIZE`](#voxatile_max_tile_size) environment variable.
- `--modules-path <path>` Path to modules directory (see [modules](#modules)).

`<outputPath>`: generation output path (must be an existing and writable directory).

Output path and generation parameters have to be provided.

## Environment variables

### `VOXATILE_PARAMS`

If set, may contain generation parameters to use when `--param-file` command line option is absent.

### `VOXATILE_MAX_TILE_SIZE`

If set to a positive integer, perform tiled generation with tiles no larger than this number. Overridden by `--max-tile-size` command line option.

### `VOXATILE_MODULES_PATH`

Path to modules directory (see [modules](#modules)).

## Compile and run

If you have cloned the project repository, you can [build the JAR using Maven](../tools/Maven.md#create-an-executable-jar) and run it:
```shell
mvn -Dmaven.test.skip=true clean package && java -jar target/Generator.jar -p parameters.yaml $HOME/.minetest/worlds/voxatile
```

## Download workflow artifact

> [!CAUTION]
> Please review the code first before running the JAR if it comes from a PR from an untrusted source!

If you just want to test the JAR from a different branch (e.g. to validate a PR), you can just download the [JAR built by the GitHub workflow](../tools/GitHub-workflows.md#build-jar), available as an artifact. You can find the link in the PR discussion or in the commit comments if no PR is open for the desired branch.

Once you downloaded (and extracted) the artifact, you should have the `Generator.jar` file and will be able to run it:
```shell
java -jar Generator.jar -p parameters.yaml $HOME/.minetest/worlds/voxatile
```

## Run using Maven

If, for some reason, you don't want to build the JAR, you can run the [`exec:java`](https://www.mojohaus.org/exec-maven-plugin/java-mojo.html) Maven goal:
```shell
mvn clean compile exec:java \
  -Dexec.cleanupDaemonThreads=false \
  -Dexec.mainClass="com.ignfab.minalac.generator.Voxatile" \
  -Dexec.args="-p parameters.yaml $HOME/.minetest/worlds/voxatile"
```

# Modules

Modules are Jar files adding features to generator (output formats, source types, task types, ...).

They should be placed in a directory specified either by `--modules-path` command line option or `VOXATILE_MODULES_PATH` environment variable. If command line option is set, environment variable is ignored. If none set, no modules will be loaded.

All Jar files in modules directory, but not in subdirectories (no recursive loading), will be loaded. Beware that presence of Jar file that is not a module in that directory will make generation fail.

Once modules loaded, corresponding features can be used in parameters.

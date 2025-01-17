# Run

> [!IMPORTANT]
> Don't forget to configure your [proxy](../Proxy.md) if needed!

This page is about different methods for building and running Generator. They are provided for informational purpose. If you just want to run the generator, please refer to [Run.md](Run.md).

## Generation parameters

Generation involves too many parameters to be passed on command line. They are passed either by file using `-p`/`--param-file` command line option or using `MINALAC_PARAMS` environment variable.

Refer to [generation parameters documentation](GenerationParameters.md) for further information.

To avoid useless efforts building a parameter file, you can use provided `generate.sh` script (see [Run.md](Run.md#generatesh)).

## Command line arguments
Usage:
```
java -jar Generator.jar [OPTIONS] <outputPath>
```

Options:
- `-h` or `--help` display command usage.
- `-p <path>` or `--param-file <path>` read generation parameters from given path. If omitted, parameters are read from `MINALAC_PARAMS` environment variable.
- `--generation-disabled` Stop before starting generation, after parameters parsed.
- `--save-disabled` Stop before saving output file, after generation done.

`<outputPath>`: generation output path (must be an existing and writable directory).

Output path and generation parameters have to be provided.

## Compile and run

If you have cloned the project repository, you can [build the JAR using Maven](../tools/Maven.md#create-an-executable-jar) and run it:
```shell
mvn -Dmaven.test.skip=true clean package && java -jar target/Generator.jar -p parameters.yaml $HOME/.minetest/worlds/minalac
```

## Download workflow artifact

> [!CAUTION]
> Please review the code first before running the JAR if it comes from a PR from an untrusted source!

If you just want to test the JAR from a different branch (e.g. to validate a PR), you can just download the [JAR built by the GitHub workflow](../tools/GitHub-workflows.md#build-jar), available as an artifact. You can find the link in the PR discussion or in the commit comments if no PR is open for the desired branch.

Once you downloaded (and extracted) the artifact, you should have the `Generator.jar` file and will be able to run it:
```shell
java -jar Generator.jar -p parameters.yaml $HOME/.minetest/worlds/minalac
```

## Run using Maven

If, for some reason, you don't want to build the JAR, you can run the [`exec:java`](https://www.mojohaus.org/exec-maven-plugin/java-mojo.html) Maven goal:
```shell
mvn clean compile exec:java \
  -Dexec.cleanupDaemonThreads=false \
  -Dexec.mainClass="com.ignfab.minalac.generator.MinalacGenerator" \
  -Dexec.args="-p parameters.yaml $HOME/.minetest/worlds/minalac"
```

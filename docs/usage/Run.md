# Run

> [!IMPORTANT]
> Don't forget to configure your [proxy](../Proxy.md) if needed!

## TL;DR;

Run and compile from repository root:

```shell
mvn -DskipTests=true clean package && java -jar target/Generator.jar -p examples/default.yaml $HOME/.minetest/worlds/minalac
```

Run jar only:

```shell
java -jar Generator.jar -p examples/default.yaml $HOME/.minetest/worlds/minalac
```

This command assume you already have the `Generator.jar` file in your current working directory. If this is not the case, see instructions below and pick the one that best fits your case.

## Generation parameters

Generation involves too many parameters to be passed on command line. They are passed either by file using `-p`/`--param-file` command line option or using `MINALAC_PARAMS` environment variable.

Refer to [generation parameters documentation](GenerationParameters.md) for further information.

## Command line arguments

`-h` `--help` display command usage.
`-p path` `--param-file path` read generation parameters from given path. If omitted, parameters are read from `MINALAC_PARAMS` environment variable.
`outputPath` generation output path (required).

Output path and generation parameters has to be provided.

## Compile and run

If you have cloned the project repository, you can [build the JAR using Maven](../tools/Maven.md#create-an-executable-jar) and run it:
```shell
mvn -DskipTests=true clean package && java -jar target/Generator.jar -p examples/default.yaml $HOME/.minetest/worlds/minalac
```

## Download workflow artifact

> [!CAUTION]
> Please review the code first before running the JAR if it comes from a PR from an untrusted source!

If you just want to test the JAR from a different branch (e.g. to validate a PR), you can just download the [JAR built by the GitHub workflow](../tools/GitHub-workflows.md#build-jar), available as an artifact. You can find the link in the PR discussion or in the commit comments if no PR is open for the desired branch.

Once you downloaded (and extracted) the artifact, you should have the `Generator.jar` file and will be able to run it:
```shell
java -jar Generator.jar -p examples/default.yaml $HOME/.minetest/worlds/minalac
```

## Run using Maven

If, for some reason, you don't want to build the JAR, you can run the [`exec:java`](https://www.mojohaus.org/exec-maven-plugin/java-mojo.html) Maven goal:
```shell
mvn clean compile exec:java \
  -Dexec.cleanupDaemonThreads=false \
  -Dexec.mainClass="com.ignfab.minalac.generator.SampleImplementation" \
  -Dexec.args="-p examples/default.yaml $HOME/.minetest/worlds/minalac"
```

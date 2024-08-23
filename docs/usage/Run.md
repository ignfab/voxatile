# Run

> [!IMPORTANT]
> Don't forget to configure your [proxy](../Proxy.md) if needed!

<!-- TODO Update options with configuration instead of command line arguments -->
The generator takes input options to configure the work to do. All the snippet below will use the following options:
```shell
options="$HOME/.minetest/worlds/minalac EPSG:2154 600000 6340000 1000 1000 1.0 10.0 minetest"
```

For parameters explanation, refer to comments in [`SampleImplementation`](../../src/main/java/com/ignfab/minalac/generator/SampleImplementation.java) class.

The simplest way to run the generator is to run the executable JAR file:
```shell
java -jar Generator.jar $options
```

This command assume you already have the `Generator.jar` file in your current working directory. If this is not the case, see instructions below and pick the one that best fits your case.

## Compile and run

If you have cloned the project repository, you can [build the JAR using Maven](../tools/Maven.md#create-an-executable-jar) and run it:
```shell
mvn -Dmaven.test.skip=true clean package && java -jar target/Generator.jar $options
```

## Download workflow artifact

> [!CAUTION]
> Please review the code first before running the JAR if it comes from a PR from an untrusted source!

If you just want to test the JAR from a different branch (e.g. to validate a PR), you can just download the [JAR built by the GitHub workflow](../tools/GitHub-workflows.md#build-jar), available as an artifact. You can find the link in the PR discussion or in the commit comments if no PR is open for the desired branch.

Once you downloaded (and extracted) the artifact, you should have the `Generator.jar` file and will be able to run it:
```shell
java -jar Generator.jar $options
```

## Run using Maven

If, for some reason, you don't want to build the JAR, you can run the [`exec:java`](https://www.mojohaus.org/exec-maven-plugin/java-mojo.html) Maven goal:
```shell
mvn clean compile exec:java \
  -Dexec.cleanupDaemonThreads=false \
  -Dexec.mainClass="com.ignfab.minalac.generator.SampleImplementation" \
  -Dexec.args="$options"
```

# Maven

This project uses [Maven](https://maven.apache.org) as build tool. If you're already familiar with this tool, skip to the [Usage](#Usage) section.

## What is Maven?

Maven is an external tool that manages project dependencies and build (along other tasks related to project management). You should follow the official documentation to get download link and installation instructions: <https://maven.apache.org>.

The main command is `mvn`, and is often followed by one or more goals/phases. This command should always be executed in the same directory containing the `pom.xml` file. If this is not possible, you can use the `-f path/to/pom.xml` option.

### Project lifecycle

When running Maven on the project, it goes through **phases**. Each phase should answer a specific need. They are executed in order, until the one we specified in our command.

- `clean`: Delete the whole `target` directory. *Optional*
  - `target/**` => *deleted*
- `validate`: Check the project is OK and the build can be done.
- `compile`: Process all source files in the `src/main` folder and compile them.
  - `src/main/java/**.java` => `target/classes/**.class`
  - `src/main/resources/**` => `target/classes/**`
- `test`: Process all test files in the `src/test` folder and compile them.
  - `src/test/java/**.java` => `target/test-classes/**.class`
  - `src/test/resources/**` => `target/test-classes/**`
- `package`: Bundle compiled files into an executable JAR.
  - `target/classes/**` => `target/<name>.jar`
- `verify`: Execute post-build checks to ensure it went well.
- `install`: Copy the artifacts (the JAR) to the local `.m2` repository, allowing using this project as a dependency of another one.
- `site`: Process all site files in the `src/site` folder and build the Maven site of this project.
  - `src/site` => `target/site`
- `deploy` / `site-deploy`: Publish the artifacts (or site) to a remote Maven repository, allowing others to use this project as a dependency / view the project's site.

### Plugins

Maven is built around plugins, allowing extra functionality to be integrated in this workflow. Each plugin exposes **goals**, representing a task. These goals can be executed individually or bound to an existing phase.

Some well-known plugins includes:
- Maven Surefire Plugin: Execute unit tests and report results (bound to the `test` phase).
- Maven Checkstyle Plugin: Validate code-style rules (executed standalone or bound to the `validate` or the `verify` phase).
- Maven Javadoc Plugin: Process Javadoc Comments and create a static HTML version (executed standalone).

## Usage

The project is configured in the [pom.xml](../../pom.xml) file. We use the default lifecycle until the `verify` phase.

- The [`maven-surefire-plugin`](https://maven.apache.org/surefire/maven-surefire-plugin/) is bound to the `test` phase (goal: [`surefire:test`](https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html)).
- The [`maven-shade-plugin`](https://maven.apache.org/plugins/maven-shade-plugin/) is bound to the `package` phase (goal: [`shade:shade`](https://maven.apache.org/plugins/maven-shade-plugin/shade-mojo.html)).
- The [`maven-checkstyle-plugin`](https://maven.apache.org/plugins/maven-checkstyle-plugin/) is bound to the `verify` phase (goal: [`checkstyle:check`](https://maven.apache.org/plugins/maven-checkstyle-plugin/check-mojo.html)).
- The [`maven-javadoc-plugin`](https://maven.apache.org/plugins/maven-javadoc-plugin/) is not bound to any phase (goal: [`javadoc:javadoc`](https://maven.apache.org/plugins/maven-javadoc-plugin/javadoc-mojo.html)).

Depending on what you want to do, you may use one of the following command. It is recommended to always run the `clean` phase to ensure you are not mixing previous builds together.

> [!IMPORTANT]
> Don't forget to configure your [proxy](../Proxy.md) if needed!

### Compile without JAR

To quickly check that the project compiles (after refactoring a variable name, for example), you can run the `compile` phase. However, it won't [produce an executable JAR](#create-an-executable-jar) file, and will only allow you to [run the project using Maven](../usage/Generator.md#run-using-maven)!

```shell
mvn clean compile
```

### Run unit tests

To compile and execute [unit tests](JUnit.md) (after modifying a function, for example), you can run the `test` phase. It will compile the source and tests, and you will get a report about the success / failure at the end. The results are also saved in the `target/surefire-reports` directory for later analysis or to use them in an external tool.

```shell
mvn clean test
```

You can execute only a subset of unit tests (to quickly debug your code, for example) by using the `-Dtest=<pattern>` parameter. Example:
```shell
mvn -Dtest="my.package.*" clean test
mvn -Dtest="my.package.MyClassTest" clean test
mvn -Dtest="my.package.MyClassTest#testMyMethod" clean test
```

### Create an executable JAR

To build the project into a dependency-shaded, executable JAR (to [execute it](../usage/Run.md) locally, for example), you can run the `package` phase. It will compile the source and bundle all the dependencies into a single JAR (`target/Generator.jar`).

Because this phase is after the `test` one, they will be executed as well. If you want to speed up the process, you can explicitly ignore them using `-DskipTests=true`.

```shell
# With tests
mvn clean package

# Without tests
mvn -DskipTests=true clean package
```

### Check code-style

To check that your files comply with the [code-style rules](Checkstyle.md) (before commiting your changes, for example), you can run the `verify` phase. It will use the `checkstyle.xml` file and validate that everything is fine, and you will get a report about the violations at the end. The results are also saved in the `target/checkstyle-result.xml` file for later analysis or to use them in an external tool.

The previous phases will be executed as well, so you can choose to skip tests to speed up the process. However, because the purpose of this is to ensure everything is OK, it may be a good idea to let them run as well.

```shell
# With tests
mvn clean verify

# Without tests
mvn -DskipTests=true clean verify
```

### Generate Javadoc

To generate static [Javadoc](Javadoc.md) HTML pages (after updating a comment, for example), you can run the `javadoc:javadoc` goal. It will parse the Java files looking for Javadoc Comments, and include them into the `target/apidocs` result.

```shell
mvn javadoc:javadoc
```

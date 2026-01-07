# Module development

Generator features can be extended with modules. Typical usage of modules is adding new task types (see [Generator.md](../usage/Generator.md#modules)).

## Set up development environment

Module project must have a dependency to `Generator.jar`. This file could be copied locally but it's preferable to tell `maven` where to fetch it from.

In `pom.xml`, add following dependency:
```xml
<dependency>
    <groupId>com.ignfab</groupId>
    <artifactId>minalac-generator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Don't forget the `<scope>provided</scope>` to avoid shading the generator classes into the module's JAR file!

Add eventual corresponding `repository`.

If you use a `Generator.jar` from a GitHub repository, you should create a personal access token (classic) with `read:package` permission and modify your `~/.m2/settings.xml` according to [GitHub instructions](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token).

## Minimal module

Minimal module consists of two files:
* A module class inheriting from `Module`;
* A property file, named `module.properties`, containing a `class` property which gives canonical name of that subclass.

Example of `HelloWorldModule` subclass:

```java
package com.ignfab.minalac.helloworld;

import com.ignfab.minalac.generator.Module;

public class HelloWorldModule extends Module {
    public HelloWorldModule() {
        System.out.println("Hello World!");
    }
}
```

This class **must** have a public no-argument constructor that will be used by the generator to instantiate it!

Example of corresponding `module.properties`:

```properties
class=com.ignfab.minalac.helloworld.HelloWorldModule
```

Now, a module Jar file has to be created from these two files. This is achieved with usual build tools like [Maven](https://maven.apache.org/) or [Gradle](https://gradle.org/). The only specific need is that `Generator.jar` should be added as a compile-only dependency (and not included in final module Jar).

Once Jar created, put it in the appropriate module directory and you are done!

## Go further

`Module` subclass is the module entrypoint. In that class, it is possible to add new capabilities to Generator by making it able to understand new parameters.

Override `registerParams` method to add, for example, new tasks:
```java
@Override
public void registerParams(ParamsParser parser) {
    parser.registerParams("myOperation", MyOperationTaskParams.class);
}
```

Of course, `MyOperationTaskParams` class and probably other classes have to be defined in the module project. Classes from the Generator jar may also be used.

Refer to [parameters](Parameters.md) documentation for further information.

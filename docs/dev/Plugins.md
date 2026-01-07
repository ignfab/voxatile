# Plugin development

Generator functionnalities can be extanded with plugins. Typical usage of plugins is adding new task types.

Some plugins are included in Generator but many other can be provided as separated Jar files.

## Plugin usage

To add plugins to generator, simply put their Jar files into a directory and give that directory path to the generator.
This can be achieved in two ways:
* Use `--plugins-path` command line option with directory path;
* Define `MINALAC_PLUGINS_PATH` environment variable so it contains directory path;

All Jar files in directory will be included as plugins (beware, presence of Jar file that is not a plugin in that directory will make generation fail).

## Plugin development
### Set up development environment
Plugin project must have a dependency to `Generator.jar`. This file could be copied locally but it's preferable to tell `maven` where to fetch it from.

In `pom.xml`, add following dependency:
```xml
<dependency>
    <groupId>com.ignfab</groupId>
    <artifactId>minalac-generator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

If you use a `Generator.jar` from a private github repository, you should create a personal access token (classic) with `read:package` permission and modify your `~/.m2/settings.xml` according to [Github instructions](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token).

### Minimal plugin

Minimal plugin consists in two files:
* A plugin class derivated from `Plugin`;
* A property file, named `plugin.properties`, containing a `class` property wich gives canonical name of that class.

Example of `HelloWorldPlugin` derivated class:
```java
package com.ignfab.minalac.helloworld;

import com.ignfab.minalac.generator.Plugin;

public class HelloWorldPlugin extends Plugin {

    @Override
    public void init() {
        System.out.println("Hello World!");
    }
}
```

Exemple of corresponding `plugin.properties`:
```
class=com.ignfab.minalac.helloworld.HelloWorldPlugin
```

Now, a plugin Jar file has to be created from these two files. This is achieved with usual build tools like [Maven](https://maven.apache.org/) or [Gradle](https://gradle.org/). The only specific need is that `Generator.jar` should be added as a dependancy (but not included in final plugin Jar).

Once Jar created, put it in the appropriate plugin directory and you are done!

### Go further

`Plugin` derivated class is the plugin entrypoint. In that class, it is possible to add new capabilities to Generator by making it able to understand new parameters.

Override `registerParams` method to add, for example, new tasks:
```java
@Override
public void registerParams(ParamsParser parser) {
    parser.registerParams("myOperation", MyOperationTaskParams.class);
}
```

Of course, `MyOperationTaskParams` class and probably other classes have to be defined in the plugin project. Classes from the Generator jar may also be used.

Refer to [parameters](Parameters.md) documentation for further information.

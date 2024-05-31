# How to use that stuff
Place yourself into proper directory:
```shell
cd minalac-generator
```

If behind a proxy, don't forget to:
```shell
export JAVA_OPTS="-Dhttp.proxyHost=proxy.ign.fr -Dhttp.proxyPort=3128 -Dhttps.proxyHost=proxy.ign.fr -Dhttps.proxyPort=3128"
# For Oracle Java:
export JAVA_TOOL_OPTIONS=$JAVA_OPTS
# For Maven:
export MAVEN_OPTS=$JAVA_OPTS
```

## Compile
```shell
mvn clean package
```

## Test
```shell
mvn test
```

## Validate code-style
```shell
mvn verify
```

## Execute
```shell
mvn package && java -jar ./target/Generator.jar $HOME/.minetest/worlds/minalac EPSG:2154 600000 6340000 1000 1000 1.0 10.0 minetest
```

For parameters explanation, refer to comments in `SampleImplementation` class.


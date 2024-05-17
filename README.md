# Compile

```
cd minalac-generator
mvn compile
```

# Test

```
cd minalac-generator
mvn test
```

# Execute
```
cd minalac-generator
mvn compile && mvn exec:java -Dexec.mainClass="com.ignfab.minalac.generator.SampleImplementation" -Dexec.args="$HOME/.minetest/worlds/minalac EPSG:2154 600000 6340000 1000 1000 1.0 10.0"
```

If behind a proxy, don't forget to:
```
export MAVEN_OPTS="-Dhttp.proxyHost=proxy.ign.fr -Dhttp.proxyPort=3128 -Dhttps.proxyHost=proxy.ign.fr -Dhttps.proxyPort=3128"
```
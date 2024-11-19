# Minalac Generator

<!-- TODO Explain briefly the project -->

## Simple usage

> [!NOTE]
> Read the documentation about [how to run this project](docs/usage/Run.md) for more options.

### From sources

Clone the repository:
```shell
git clone https://github.com/ignfab/minalac-generator.git
cd minalac-generator # All commands are always based on the project root
```

Compile and run using Maven:

```shell
mvn -DskipTests=true clean package && java -jar target/Generator.jar -p examples/default.yaml $HOME/.minetest/worlds/minalac
```

Make sure you're using a JDK whose version is equal to or later than the target compiler version specified in `pom.xml`. For example, for this current release :

```shell
sudo apt install openjdk-17-jdk # install JDK matching target compiler version or higher
mvn -v # check maven is using the new JDK
```

<!--
### From pre-built JAR

TODO Download JAR from latest release and run it with default params -->

## License

This project is licensed under the terms of the [GNU AGPL v3](LICENSE.txt) license.

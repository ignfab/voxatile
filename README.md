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
<!--
### From pre-built JAR

TODO Download JAR from latest release and run it with default params -->

## License

This project is licensed under the terms of the [GNU AGPL v3](LICENSE.txt) license.

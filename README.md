# Voxatile

*The Swiss Army knife of geodata voxelization.*

Voxatile is a geo-voxelizer. It can turn geographical data into voxels (small cubes).

> [!WARNING]
> Before the opening, the project was named "Minalac generator".
> It is being renamed to "Voxatile".
> You may find both names in code and documentation, keep in mind that they both refer to the same thing.
> Old name occurrences will be progressively replaced, starting with the package name.

## Project status

The tool is available in very early alpha version. It is still being actively developed, and many changes will occur in parameters syntax, along with new features being added.

## Simple usage

> [!NOTE]
> Read the documentation about [how to run this project](docs/usage/Run.md) for more options.
> "How To" guides are being written and will be available soon.

### From sources

Clone the repository:
```shell
git clone https://github.com/ignfab/voxatile.git
cd voxatile # All commands are always based on the project root
```

Compile and run using Maven (Unix only):
```shell
mvn -Dmaven.test.skip=true clean package && ./generate.sh minetest full ign $HOME/.minetest/worlds/minalac
```

<!--
### From pre-built JAR

TODO Download JAR from latest release and run it with default params -->

## License

This project is licensed under the terms of the [GNU AGPL v3](LICENSE.txt) license.

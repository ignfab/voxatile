# Heightmaps

Heightmaps are 2d array of height (integer) data. They are of two main sorts:
* Stored heightmaps, in which data can be read and written;
* Computed heightmaps, in which data can only be read;

As generation is tiled, stored heightmap data only exist within a tile (a `GenerationTile`) and is deleted once tile generated. When not within a tile, only heightmap specifications may be accessed.

## Heightmaps in a TiledTask

A `TiledTask` will run on various `GenerationTile` so it does not have a direct access do heightmap data. It has heightmap specifications and gets the "real" heightmaps from the tile. Specifications are of two kinds:
* `ReadableHeightmapSpec` for read only heightmaps (includes all computed heightmaps);
* `WritableHeightmapSpec` for writable heightmaps (only stored heightmaps for now);

Once within a `GenerationTile`, heightmaps could be accessed using tile `heightmaps().get()` method.

Here is an example how to use readable and writable heightmaps in a tile task:

```java
public class MyHeightmapTask extends TiledTask {
    // In tile task, we store only heightmaps specs:
    private final ReadableHeightmapSpec readableSpec;
    private final WritableHeightmapSpec writableSpec;

    ...

    @Override
    protected void run(GenerationTile tile) {
        // Now we got a tile, we can access heightmaps:
        ReadableHeightmap readable = tile.heightmaps().get(readableSpec);
        WritableHeightmap writable = tile.heightmaps().get(writableSpec);

        ...
        h1 = readable.get(x, y);
        h2 = writable.get(x, y); // Writable heightmap is also readable
        writable.set(x, y, h1 + h2);
        ...
    }

    ...
}
```

Heightmaps are cached so `heightmaps().get()` can be called many times with the same specs without performances issues.

## Heightmaps in parameters

Declaring usage of heightmaps in parameters is quite straightforward. Use a `ReadableHeightmapParams` for readable heightmaps (this will manage all kinds of computed heightmaps) and `WritableHeightmapParams` for writable heightmap (only stored heightmaps).

`create()` method of these classes will not directly create heightmaps but only heightmap specs (which have to be passed to a tile `heightmaps().get()` method in order to get the real heightmap to use).

```java
public class MyTaskParams extends TileTaskParams {

    public ReadableHeightmapParams readable;
    public WritableHeightmapParams writable;

    ...

    public TileTask create(Generation generation) {
        return new MyTask(
            ...
            readable.create(generation.heightmaps()),
            writable.create(generation.heightmaps()),
            ...
        );
    }

    ...
}
```

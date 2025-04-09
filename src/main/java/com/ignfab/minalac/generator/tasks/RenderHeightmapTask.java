package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A {@link TileTask} placing things between a minimum and maximum heightmap.
 */
public class RenderHeightmapTask implements TileTask {
    private final ReadableHeightmap minimum;
    private final ReadableHeightmap maximum;
    private final Placeable placeable;

    /**
     * Creates a new {@code RenderHeightmapTask}.
     *
     * @param minimum the minimum heightmap.
     * @param maximum the maximum heightmap.
     * @param placeable the material to place.
     */
    public RenderHeightmapTask(ReadableHeightmap minimum, ReadableHeightmap maximum, Placeable placeable) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.placeable = placeable;
    }

    @Override
    public void run(VoxelTile tile) {
        if (minimum == maximum)
            for (WorldCoords2d c : tile.limits().to2d().intersection(maximum.bbox()))
                placeable.place(tile, c.x(), c.y(), maximum.get(c));
        else
            for (WorldCoords2d c : tile.limits().to2d().intersection(minimum.bbox()).intersection(maximum.bbox()))
                for (int z = minimum.get(c); z <= maximum.get(c); z++)
                    placeable.place(tile, c.x(), c.y(), z);
    }
}

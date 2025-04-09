package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * This renderer places the placeable between a minimum and maximum heightmap.
 */
public class HeightmapRenderer implements Renderer {
    private final ReadableHeightmap minimum;
    private final ReadableHeightmap maximum;
    private final Placeable placeable;

    /**
     * Creates a new {@code HeightmapRenderer}.
     *
     * @param minimum the minimum heightmap.
     * @param maximum the maximum heightmap.
     * @param placeable the material to place.
     */
    public HeightmapRenderer(ReadableHeightmap minimum, ReadableHeightmap maximum, Placeable placeable) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.placeable = placeable;
    }

    @Override
    public void render(VoxelWorld world) {
        if (minimum == maximum)
            for (WorldCoords2d c : world.limits().to2d().intersection(maximum.bbox()))
                placeable.place(world, c.x(), c.y(), maximum.get(c));
        else
            for (WorldCoords2d c : world.limits().to2d().intersection(minimum.bbox()).intersection(maximum.bbox()))
                for (int z = minimum.get(c); z <= maximum.get(c); z++)
                    placeable.place(world, c.x(), c.y(), z);
    }
}

package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundReadableHeightmap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * This renderer places the placeable between a minimum and maximum heightmap.
 */
public class HeightmapRenderer implements Renderer {
    private final UnboundReadableHeightmap minimum;
    private final UnboundReadableHeightmap maximum;
    private final Placeable placeable;

    /**
     * Creates a new {@code HeightmapRenderer}.
     *
     * @param minimum the minimum heightmap.
     * @param maximum the maximum heightmap.
     * @param placeable the material to place.
     */
    public HeightmapRenderer(UnboundReadableHeightmap minimum, UnboundReadableHeightmap maximum, Placeable placeable) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.placeable = placeable;
    }

    @Override
    public void render(GenerationTile tile) {
        if (minimum == maximum) {
            ReadableHeightmap heightmap = maximum.bind(tile);
            for (WorldCoords2d c : tile.limits().to2d().intersection(heightmap.bbox()))
                placeable.place(tile.worldTile(), c.x(), c.y(), heightmap.get(c));
        } else {
            ReadableHeightmap minimum = this.minimum.bind(tile);
            ReadableHeightmap maximum = this.maximum.bind(tile);
            for (WorldCoords2d c : tile.limits().to2d().intersection(minimum.bbox()).intersection(maximum.bbox()))
                for (int z = minimum.get(c); z <= maximum.get(c); z++)
                    placeable.place(tile.worldTile(), c.x(), c.y(), z);
        }
    }
}

package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.world2d.WorldCoords2d;

/**
 * A {@link TileTask} placing things between a minimum and maximum heightmap.
 */
public class RenderHeightmapTask implements TileTask {
    private final ReadableHeightmapSpec minimum;
    private final ReadableHeightmapSpec maximum;
    private final Placeable placeable;

    /**
     * Creates a new {@code RenderHeightmapTask}.
     *
     * @param minimum the minimum heightmap.
     * @param maximum the maximum heightmap.
     * @param placeable the material to place.
     */
    public RenderHeightmapTask(ReadableHeightmapSpec minimum, ReadableHeightmapSpec maximum, Placeable placeable) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.placeable = placeable;
    }

    @Override
    public void run(GenerationTile tile) {
        if (minimum == maximum) {
            ReadableHeightmap heightmap = tile.heightmap(maximum);
            for (WorldCoords2d c : tile.limits().to2d().intersection(heightmap.bbox()))
                placeable.place(tile.voxels(), c.x(), c.y(), heightmap.get(c));
        } else {
            ReadableHeightmap minimum = tile.heightmap(this.minimum);
            ReadableHeightmap maximum = tile.heightmap(this.maximum);
            for (WorldCoords2d c : tile.limits().to2d().intersection(minimum.bbox()).intersection(maximum.bbox()))
                for (int z = minimum.get(c); z <= maximum.get(c); z++)
                    placeable.place(tile.voxels(), c.x(), c.y(), z);
        }
    }
}

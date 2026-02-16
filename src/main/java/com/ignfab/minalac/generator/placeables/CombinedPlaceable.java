package com.ignfab.minalac.generator.placeables;

import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A placeable that is a combination of placeables.
 * Placeable will be placed the order they were added.
 */
public class CombinedPlaceable implements Placeable {
    private final List<Placeable> placeables = new LinkedList<>();
    private WorldBBox3d bbox;

    /**
     * Add a placeable at the end of the {@code CombinedPlaceable}'s placeables list.
     *
     * @param placeable Placeable to add
     */
    public void add(Placeable placeable) {
        placeables.add(placeable);
        bbox = null; // Force recompute bbox
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        placeables.forEach((placeable) -> placeable.place(tile, x, y, z));
    }

    @Override
    public WorldBBox3d bbox() {
        if (bbox == null)
            bbox = WorldBBox3d.surrounding(placeables);

        return bbox;
    }
}

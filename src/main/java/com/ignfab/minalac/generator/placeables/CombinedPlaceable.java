package com.ignfab.minalac.generator.placeables;

import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A placeable that is a combination of placeables.
 * Placeable will be placed the order they were added.
 */
public class CombinedPlaceable implements Placeable {
    private final List<Placeable> placeables = new LinkedList<>();

    /**
     * Add a placeable at the end of the {@code CombinedPlaceable}'s placeables list.
     *
     * @param placeable Placeable to add
     */
    public void add(Placeable placeable) {
        placeables.add(placeable);
    }

    @Override
    public void place(VoxelWorld world, int x, int y, int z) {
        placeables.forEach((placeable) -> placeable.place(world, x, y, z));
    }
}

package com.ignfab.minalac.generator.placeables;

import java.util.LinkedList;
import java.util.List;

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
    public void place(int x, int y, int z) {
        placeables.forEach((placer) -> placer.place(x, y, z));
    }
}

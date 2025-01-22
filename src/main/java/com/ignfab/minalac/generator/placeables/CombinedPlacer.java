package com.ignfab.minalac.generator.placeables;

import java.util.LinkedList;
import java.util.List;

/**
 * A placer that is a combination of placers.
 * Placers are applied in the order they were added.
 */
public class CombinedPlacer implements Placer {

    private final List<Placer> placers = new LinkedList<>();

    /**
     * Add a placer at the end of the {@code CombinedPlacer}'s placers list.
     *
     * @param placer Placer to add
     */
    public void add(Placer placer) {
        placers.add(placer);
    }

    @Override
    public void place(int x, int y, int z) {
        placers.forEach((placer) -> placer.place(x, y, z));
    }
}

package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.world.Placeable;

/**
 * A structure that has a size and from which placeables can be gotten.
 */
public interface Structure3d extends Bounded3d {

    /**
     * Returns placeable at given position in the structure.
     *
     * @param position Position of placeable to get
     * @return placeable at given position or null if none.
     */
    Placeable get(WorldCoords3d position);
}

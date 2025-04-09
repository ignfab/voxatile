package com.ignfab.minalac.generator.parameters.placeables;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Places nothing.
 */
public class NothingParams extends PlaceableParams {
    @Override
    public Placeable create(Seed seed) {
        return Nothing.INSTANCE;
    }
}

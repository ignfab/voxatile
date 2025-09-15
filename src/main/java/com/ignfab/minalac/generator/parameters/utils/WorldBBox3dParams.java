package com.ignfab.minalac.generator.parameters.utils;

import java.util.ArrayList;

import com.ignfab.minalac.generator.parameters.Params;
import com.ignfab.minalac.generator.utils.IntegerInterval;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Parameters describing a {@link WorldBBox3d}.
 * <p>
 * A BBox can be described as an array of tree coordinates or coorinates intervals (in x, y, z order).
 */
public class WorldBBox3dParams extends ArrayList<IntegerIntervalParams> implements Params {
    @Override
    public void validate() {
        if (size() != 3)
            throw new IllegalArgumentException("3d box should have three coordinates");
    }

    /**
     * Creates a new {@link WorldBBox3d} out of parameters.
     *
     * @return created {@link WorldBBox3d}
     */
    public WorldBBox3d create() {
        IntegerInterval xs = get(0).create();
        IntegerInterval ys = get(1).create();
        IntegerInterval zs = get(2).create();

        return new WorldBBox3d(xs.begin(), ys.begin(), zs.begin(), xs.size(), ys.size(), zs.size());
    }
}

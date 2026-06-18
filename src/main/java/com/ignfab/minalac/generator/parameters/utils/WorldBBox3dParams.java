package com.ignfab.minalac.generator.parameters.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.ignfab.minalac.generator.utils.IntegerInterval;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Parameters describing a {@link WorldBBox3d}.
 * <p>
 * A BBox can be described as an array of three coordinates or coordinates intervals (in x, y, z order).
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record WorldBBox3dParams(IntegerIntervalParams x, IntegerIntervalParams y, IntegerIntervalParams z) {
    /**
     * Validates parameters.
     */
    public void validate() {
        x.validate();
        y.validate();
        z.validate();
    }

    /**
     * Creates a new {@link WorldBBox3d} out of parameters.
     *
     * @return created {@link WorldBBox3d}
     */
    public WorldBBox3d create() {
        IntegerInterval xs = x.create();
        IntegerInterval ys = y.create();
        IntegerInterval zs = z.create();

        return new WorldBBox3d(xs.begin(), ys.begin(), zs.begin(), xs.size(), ys.size(), zs.size());
    }
}

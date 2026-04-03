package com.ignfab.minalac.generator.parameters.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.JsonWrapper;
import com.ignfab.minalac.generator.utils.IntegerInterval;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Parameters describing a {@link WorldBBox3d}.
 * <p>
 * A BBox can be described as an array of three coordinates or coordinates intervals (in x, y, z order).
 */
@JsonWrapper
public class WorldBBox3dParams {
    /**
     * The three intervals (required).
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public List<IntegerIntervalParams> intervals;

    /**
     * Validates parameters.
     */
    public void validate() {
        if (intervals.size() != 3)
            throw new IllegalArgumentException("3d box should have three coordinates");
        intervals.get(0).validate();
        intervals.get(1).validate();
        intervals.get(2).validate();
    }

    /**
     * Creates a new {@link WorldBBox3d} out of parameters.
     *
     * @return created {@link WorldBBox3d}
     */
    public WorldBBox3d create() {
        IntegerInterval xs = intervals.get(0).create();
        IntegerInterval ys = intervals.get(1).create();
        IntegerInterval zs = intervals.get(2).create();

        return new WorldBBox3d(xs.begin(), ys.begin(), zs.begin(), xs.size(), ys.size(), zs.size());
    }
}

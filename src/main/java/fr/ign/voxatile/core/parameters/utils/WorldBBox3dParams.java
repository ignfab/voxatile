package fr.ign.voxatile.core.parameters.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.ign.voxatile.core.utils.IntegerInterval;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

/**
 * Parameters describing a {@link WorldBBox3d}.
 * <p>
 * A BBox can be described as an array of three coordinates or coordinates intervals (in x, y, z order).
 *
 * @param x coordinate interval on the x-axis
 * @param y coordinate interval on the y-axis
 * @param z coordinate interval on the z-axis
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

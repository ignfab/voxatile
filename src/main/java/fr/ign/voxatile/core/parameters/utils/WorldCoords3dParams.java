package fr.ign.voxatile.core.parameters.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;

/**
 * Parameters describing a 3d position as {@link WorldCoords3d}.
 *
 * @param x coordinate on the x-axis
 * @param y coordinate on the y-axis
 * @param z coordinate on the z-axis
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record WorldCoords3dParams(int x, int y, int z) {
    /**
     * Creates a new {@link WorldCoords3d} out of parameters.
     *
     * @return created {@link WorldCoords3d}
     */
    public WorldCoords3d create() {
        return new WorldCoords3d(x, y, z);
    }
}

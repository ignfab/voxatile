package fr.ign.voxatile.core.parameters.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.parameters.JsonWrapper;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;

/**
 * Parameters describing a 3d position as {@link WorldCoords3d}.
 */
@JsonWrapper
public class WorldCoords3dParams {
    /**
     * The three coordinates (required).
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public List<Integer> coordinates;

    /**
     * Validates parameters.
     */
    public void validate() {
        if (coordinates.size() != 3)
            throw new IllegalArgumentException("3d position should have three coordinates");
    }

    /**
     * Creates a new {@link WorldCoords3d} out of parameters.
     *
     * @return created {@link WorldCoords3d}
     */
    public WorldCoords3d create() {
        return new WorldCoords3d(coordinates.get(0), coordinates.get(1), coordinates.get(2));
    }
}

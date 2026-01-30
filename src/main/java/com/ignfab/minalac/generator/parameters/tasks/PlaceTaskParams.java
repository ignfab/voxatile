package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.PlaceTask;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Parameters for creating a {@link PlaceTask}.
 */
public class PlaceTaskParams extends TileTaskParams {
    /**
     * What to place.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;
    /**
     * Where to place.
     */
    public int x;
    /**
     * Where to place.
     */
    public int y;
    /**
     * Where to place.
     */
    public int z;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place what to place
     * @param x the place x-coordinate
     * @param y the place y-coordinate
     * @param z the place y-coordinate
     */
    @ConstructorProperties({"place", "x", "y", "z"})
    public PlaceTaskParams(PlaceableParams place, int x, int y, int z) {
        this.place = place;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void validate() {
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        WorldCoords3d pos = new WorldCoords3d(x, y, z);
        return new PlaceTask(place.create(new Seed("")), pos);
    }
}

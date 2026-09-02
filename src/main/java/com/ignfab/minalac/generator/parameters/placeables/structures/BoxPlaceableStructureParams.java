package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.utils.WorldBBox3dParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * A parameters class variant for {@link PlaceableStructure}.
 * <p>
 * Structure is described by a box filled with a placeable.
 */
public class BoxPlaceableStructureParams extends PlaceableStructureParams.Variant {

    /**
     * Box, in structure relative coordinates, where placeable should be set (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WorldBBox3dParams at;

    /**
     * Placeable to set at each position inside the given box (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Creates a new {@code PlaceableStructureParams}.
     *
     * @param at Box into which set placeables
     * @param place Placeable to set into given box
     */
    @ConstructorProperties({"at", "place"})
    public BoxPlaceableStructureParams(WorldBBox3dParams at, PlaceableParams place) {
        this.place = place;
        this.at = at;
    }

    @Override
    public void validate() {
        // Only validation propagation
        place.validate();
        at.validate();
    }

    @Override
    public void apply(Seed seed, PlaceableStructure.Builder structureBuilder) {
        structureBuilder.set(at.create(), place.create(seed));
    }
}

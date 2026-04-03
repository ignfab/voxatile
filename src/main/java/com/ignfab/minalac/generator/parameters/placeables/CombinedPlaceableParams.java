package com.ignfab.minalac.generator.parameters.placeables;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.JsonWrapper;
import com.ignfab.minalac.generator.placeables.CombinedPlaceable;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for a {@link CombinedPlaceable}.
 * CombinedPlaceable is actually represented by a list of placeables in parameter file.
 */
@JsonWrapper
public class CombinedPlaceableParams extends PlaceableParams {
    /**
     * Contained placeable params.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public List<PlaceableParams> placeableParams;

    @Override
    public void validate() {
        for (PlaceableParams placeable : placeableParams)
            placeable.validate();
    }

    @Override
    public Placeable create(Seed seed) {
        CombinedPlaceable combined = new CombinedPlaceable();
        for (PlaceableParams placeable : placeableParams)
            combined.add(placeable.create(seed));

        return combined;
    }
}

package com.ignfab.minalac.generator.parameters.placeables;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;

import com.ignfab.minalac.generator.placeables.CombinedPlaceable;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for a {@link CombinedPlaceable}.
 * CombinedPlaceable is actually represented by a list of placeables in parameter file.
 */
public class CombinedPlaceableParams extends PlaceableParams {
    /**
     * Contained placeable params.
     */
    public List<PlaceableParams> placeableParams = new LinkedList<>();

    /**
     * Creates a new {@code CombinedPlaceableParams}.
     *
     * This class is suposed to be instantiated only from {@link PlaceableParams} custom deserializer.
     *
     * @param array Array of {@code JsonNode} deserializable into {@code Placeable}
     * @param codec Codec to use to deserialize placeables
     */
    public CombinedPlaceableParams(Iterable<JsonNode> array, ObjectCodec codec) throws JsonProcessingException {
        for (JsonNode node : array)
            placeableParams.add(codec.treeToValue(node, PlaceableParams.class));
    }

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

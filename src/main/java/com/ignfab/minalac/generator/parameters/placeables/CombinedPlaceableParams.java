package com.ignfab.minalac.generator.parameters.placeables;

import java.beans.ConstructorProperties;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ignfab.minalac.generator.placeables.CombinedPlaceable;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Parameters for a {@link CombinedPlaceable}.
 * CombinedPlaceable is actually represented by a list of placeables in parameter file.
 */
 @JsonDeserialize // Avoids infinite loop, jackson reusing deserializer when deserializer tries to deserialize CustomPlaceableParams
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
    @ConstructorProperties({"placeables"})
    public CombinedPlaceableParams(Iterable<JsonNode> array, ObjectCodec codec) throws JsonProcessingException {
        for (JsonNode node : array)
            placeableParams.add(codec.treeToValue(node, PlaceableParams.class));
    }

    @Override
    public Placeable create(VoxelWorld world) {
        CombinedPlaceable combined = new CombinedPlaceable();
        for (PlaceableParams placeable : placeableParams)
            combined.add(placeable.create(world));

        return combined;
    }
}

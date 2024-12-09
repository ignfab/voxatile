package com.ignfab.minalac.generator.parameters.placeables;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ignfab.minalac.generator.parameters.placeables.structures.NoVoxelParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.StackStructureParams;

/**
 * Base class for custom placeable parameters.
 */
@JsonDeserialize // Avoids infinite loop, jackson reusing deserializer when deserializer tries to deserialize CustomPlaceableParams
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    // TODO: In another PR, register placeables in main()
    @JsonSubTypes.Type(name = "stack", value = StackStructureParams.class),
    @JsonSubTypes.Type(name = "nothing", value = NoVoxelParams.class),
})
public abstract class CustomPlaceableParams extends PlaceableParams {
}

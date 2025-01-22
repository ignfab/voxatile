package com.ignfab.minalac.generator.parameters.placeables;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Base class for custom placeable parameters.
 */
@JsonDeserialize // Avoids infinite loop, jackson reusing deserializer when deserializer tries to deserialize CustomPlaceableParams
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    // TODO: In another PR, register placeables in main()
    @JsonSubTypes.Type(name = "nothing", value = NoVoxelParams.class),
    @JsonSubTypes.Type(name = "random", value = RandomPatternParams.class),
    @JsonSubTypes.Type(name = "stack", value = StackStructureParams.class),
})
public abstract class CustomPlaceableParams extends PlaceableParams {
}

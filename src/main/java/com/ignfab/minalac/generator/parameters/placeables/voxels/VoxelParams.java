package com.ignfab.minalac.generator.parameters.placeables.voxels;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;

/**
 * Abstract class for placeables parameters creating voxels.
 */
@JsonDeserialize // Avoids infinite loop, jackson reusing deserializer when deserializer tries to deserialize CustomPlaceableParams
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type when using "default" placeable param structure
public abstract class VoxelParams extends PlaceableParams {
}

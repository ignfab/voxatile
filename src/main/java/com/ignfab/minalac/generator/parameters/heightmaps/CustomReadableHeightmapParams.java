package com.ignfab.minalac.generator.parameters.heightmaps;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Base class for all {@code ReadableHeightmap} parameters that have a specific structure.
 */
// Avoids infinite loop
@JsonDeserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(ConstantHeightmapParams.class),
    @JsonSubTypes.Type(MultiOperandsHeightmapParams.Sum.class),
    @JsonSubTypes.Type(MultiOperandsHeightmapParams.Product.class),
    @JsonSubTypes.Type(LocalMinimumHeightmapParams.class),
    @JsonSubTypes.Type(CappedManhattanHeightmapParams.class),
    @JsonSubTypes.Type(RemapHeightmapParams.class)
})
public abstract class CustomReadableHeightmapParams implements ReadableHeightmapParams {
}

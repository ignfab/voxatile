package com.ignfab.minalac.generator.parameters.placeables.layouts;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.ignfab.minalac.generator.placeables.builders.AxisStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Abstract class for all {@link AxisStructureBuilder}s.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(FixedStructureBuilderParams.class),
    @JsonSubTypes.Type(DistributedByPriorityStructureBuilderParams.class),
    @JsonSubTypes.Type(RepeatStructureBuilderParams.class),
    @JsonSubTypes.Type(StretchedStructureBuilderParams.class)
})
public abstract class AxisStructureBuilderParams {
    public void validate() {}
    public abstract AxisStructureBuilder create(Seed seed);
}

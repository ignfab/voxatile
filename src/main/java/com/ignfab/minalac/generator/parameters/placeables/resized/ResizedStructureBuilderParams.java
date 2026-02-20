package com.ignfab.minalac.generator.parameters.placeables.resized;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(FixedStructureBuilderParams.class),
    @JsonSubTypes.Type(DistributedByPriorityStructureBuilderParams.class),
    @JsonSubTypes.Type(RepeatStructureBuilderParams.class),
    @JsonSubTypes.Type(StretchedStructureBuilderParams.class)
})
public abstract class ResizedStructureBuilderParams {
    public void validate() {}
    public abstract ResizedStructureBuilder create(Seed seed);
}

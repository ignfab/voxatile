package com.ignfab.minalac.generator.parameters.placeables.layouts;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
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
public abstract class LayoutBuilderParams {
    public void validate() {}
    public abstract LayoutBuilder create(Seed seed);
}

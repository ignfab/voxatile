package com.ignfab.minalac.generator.parameters.placeables.patterns;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Main parameter class for {@link Pattern}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(RandomPatternParams.class),
    @JsonSubTypes.Type(RepeatPatternParams.class),
    @JsonSubTypes.Type(InsteadPatternParams.class),
})
public abstract class PatternParams extends PlaceableParams {

    @Override
    public abstract Pattern create(Seed seed);
}

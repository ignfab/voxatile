package com.ignfab.minalac.generator.parameters.placeables.layouts;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Interface for {@link LayoutBuilder} params.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = StructureLayoutBuilderParams.class)
@JsonSubTypes({
    @JsonSubTypes.Type(ConcatenateLayoutBuilderParams.class),
    @JsonSubTypes.Type(RepeatLayoutBuilderParams.class),
    @JsonSubTypes.Type(StructureLayoutBuilderParams.class)
})
public interface LayoutBuilderParams {

    /**
     * Validates layout builder or throws runtime exception.
     */
    default public void validate() {}

    /**
     * Creates {@link LayoutBuilder} from parameters.
     *
     * @param seed The random seed
     * @return created layout builder
     */
    public LayoutBuilder createBuilder(Seed seed);
}

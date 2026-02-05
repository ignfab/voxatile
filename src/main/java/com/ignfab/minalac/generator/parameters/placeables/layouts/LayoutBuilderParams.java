package com.ignfab.minalac.generator.parameters.placeables.layouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
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
    default void validate() {}

    /**
     * Creates {@link LayoutBuilder} from parameters.
     *
     * @param seed The random seed
     * @param policies Default axis adjustment policies
     * @return created layout builder
     * @throws UnbuildableException
     */
    LayoutBuilder createBuilder(Seed seed, AxesPolicies policies) throws UnbuildableException;

    /**
     * Axis adjustment policeis.
     */
    enum AxisPolicy {
        /**
         * Inherit policy: use default value.
         */
        @JsonProperty("inherit")
        INHERIT,

        /**
         * Keep policy: keep axis as is with no modification.
         */
        @JsonProperty("keep")
        KEEP,

        /**
         * Adjust policy: Adjust axis so it starts at 0 and has the desired size.
         */
        @JsonProperty("adjust")
        ADJUST
    };

    /**
     * Ajust policies for the three axes.
     * @param x x-axis adjustment policy
     * @param y y-axis adjustment policy
     * @param z z-axis adjustment policy
     */
    record AxesPolicies(AxisPolicy x, AxisPolicy y, AxisPolicy z) {};

}

package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for a {@link LayoutBuilder} that repeats a layout along an axis.
 */
public class RepeatLayoutBuilderParams implements LayoutBuilderParams {
    /**
     * {@link LayoutBuilderParams} to repeat.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public LayoutBuilderParams repeat;

    /**
     * Axis along which layout is repeated.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public AxisParams along;

    /**
     * Minimum number of repetitions (default 1).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int atLeast = 1;

    /**
     * Maximum number of repetitions (default infinite).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int atMost = Integer.MAX_VALUE;

    /**
     * Creates a new {@code RepeatLayoutBuilderParams} out of mandatory parameters.
     * @param repeat layout to repeat
     * @param along axis along which layout is repeated
     */
    @ConstructorProperties({ "repeat", "along" })
    public RepeatLayoutBuilderParams(LayoutBuilderParams repeat, AxisParams along) {
        this.repeat = repeat;
        this.along = along;
    }

    @Override
    public void validate() {
        if (atLeast < 0)
            throw new IllegalArgumentException("'atLeast' must be a positive integer");
        if (atMost < atLeast)
            throw new IllegalArgumentException("'atMost' must be greater than or equals to 'atLeast'");
        repeat.validate();
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed, AxesPolicies policies) throws UnbuildableLayoutException {
        return DefaultLayoutBuilder.repeat(
            repeat.createBuilder(seed, policies),
            along.create(),
            atLeast,
            atMost
        );
    }
}

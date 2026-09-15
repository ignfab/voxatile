package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.DebugLayoutTask;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Parameters for a {@code DebugStructureBuilderTask}.
 * <p>
 * This task is a helper for visualizing layouts parameters.
 */
public class DebugLayoutTaskParams extends TaskParams {
    /**
     * List of builders to use to construct structure.
     * Builders will be tried in list order. First succeeding will be used.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<LayoutBuilderParams> build;

    /**
     * Where to place resulting structure.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WorldCoords3d at;

    /**
     * Desired resulting size.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public DebugSizeParams size = new DebugSizeParams();

    /**
     * Creates a new {@code DebugLayoutTaskParams} out of mandatory parameters.
     * @param build list of builders to use
     * @param at position where to place resulting structure
     */
    @ConstructorProperties({"build", "at"})
    public DebugLayoutTaskParams(List<LayoutBuilderParams> build, WorldCoords3d at) {
        this.build = build;
        this.at = at;
    }

    @Override
    public TileTask create(Generation generation) {
        try {
            return new DebugLayoutTask(
                build.stream().map(builder -> {
                    try {
                        return builder.createBuilder(generation.seed(), new LayoutBuilderParams.AxesPolicies(
                            LayoutBuilderParams.AxisPolicy.ADJUST,
                            LayoutBuilderParams.AxisPolicy.KEEP,
                            LayoutBuilderParams.AxisPolicy.ADJUST
                        ));
                    } catch (UnbuildableException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).toList(),
                at, size.x, size.y, size.z
            );
        } catch (UnbuildableException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Params for placed structure wanted size.
     * <p>
     * Size component may be omitted. Omitting a component means leaving the builder decide size on corresponding axis.
     */
    public static class DebugSizeParams {
        /**
         * X-axis component of the size.
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public Integer x;
        /**
         * Y-axis component of the size.
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public Integer y;
        /**
         * Z-axis component of the size.
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public Integer z;
    }
}

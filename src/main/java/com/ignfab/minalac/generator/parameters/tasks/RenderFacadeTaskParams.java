package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.RenderFacadeTask;
import com.ignfab.minalac.generator.tasks.TileTask;

import tools.jackson.databind.JsonNode;

/**
 * Parameters for a {@link RenderFacadeTask}.
 */
public class RenderFacadeTaskParams extends ModelTaskParams {
    /**
     * List of builders to try out.
     * <p>
     * Builders will be tried in list order.
     * If a builder fails, next one is tried.
     * If it succeeds, facade is built and next builders won't be used.
     * If all builders fail, nothing will be built.
     */
    @JsonProperty("build")
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<LayoutBuilderParams> builders;

    /**
     * Unparsed field, good place to hold yaml anchors
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public JsonNode references;

    /**
     * Name of metadata containing building altitude (altitude of wall bottom).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams altitude;

    /**
     * Name of metadata containing building height (from wall bottom to wall top).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams height;

    /**
     * Creates a new {@code RenderFacadeTaskParams} out of mandatory parameters.
     * @param builders builders to try, in order
     * @param altitude name of metadata containing building altitude
     * @param height name of metadata containing building height
     */
    @ConstructorProperties({ "builders", "altitude", "height"})
    public RenderFacadeTaskParams(
        List<LayoutBuilderParams> builders,
        ModelValueParams altitude,
        ModelValueParams height
    ) {
        this.builders = builders;
        this.height = height;
        this.altitude = altitude;
    }

    @Override
    public void validate() {
        super.validate();
        altitude.validate();
        height.validate();
        builders.forEach(LayoutBuilderParams::validate);
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderFacadeTask(
            models.create(generation),
            // TODO: should seed rather be given when building ?
            builders.stream().map(builder -> {
                try {
                    return builder.createBuilder(generation.seed(), new LayoutBuilderParams.AxesPolicies(
                        LayoutBuilderParams.AxisPolicy.ADJUST,
                        LayoutBuilderParams.AxisPolicy.KEEP,
                        LayoutBuilderParams.AxisPolicy.ADJUST
                    ));
                } catch (UnbuildableException e) {
                    throw new IllegalArgumentException(e);
                }
            }).collect(Collectors.toList()),
            altitude.create(generation),
            height.create(generation)
        );
    }
}

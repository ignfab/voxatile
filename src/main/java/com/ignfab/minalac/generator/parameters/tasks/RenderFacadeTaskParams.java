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
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.RenderFacadeTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a {@link RenderFacadeTask}.
 */
public class RenderFacadeTaskParams extends TileTaskParams {
    /**
     * Type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

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
     * Name of metadata containing building height (from wall bottom to wall top).
     */
    // TODO: We may need an expression here
    @JsonSetter(nulls = Nulls.FAIL)
    public String height;

    /**
     * Name of metadata containing building altitude (altitude of wall bottom).
     */
    // TODO: We may need an expression here
    @JsonSetter(nulls = Nulls.FAIL)
    public String altitude;

    /**
     * Creates a new {@code RenderFacadeTaskParams} out of mandatory parameters.
     * @param models model selection for facades
     * @param builders builders to try, in order
     * @param height name of metadata containing building height
     * @param altitude name of metadata containing building altitude
     */
    @ConstructorProperties({ "models", "builders", "height", "altitude"})
    public RenderFacadeTaskParams(
        ModelSelectionParams models,
        List<LayoutBuilderParams> builders,
        String height,
        String altitude
    ) {
        this.models = models;
        this.builders = builders;
        this.height = height;
        this.altitude = altitude;
    }


    @Override
    public void validate() {
        if (height.isBlank())
            throw new IllegalArgumentException("Height metadata cannot be blank");
        if (altitude.isBlank())
            throw new IllegalArgumentException("Altitude metadata cannot be blank");
        models.validate();
        builders.forEach(LayoutBuilderParams::validate);
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderFacadeTask(
            models.create(),
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
            height,
            altitude
        );
    }
}

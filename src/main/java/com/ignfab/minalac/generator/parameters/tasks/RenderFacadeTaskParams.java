package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.tasks.RenderFacadeTask;
import com.ignfab.minalac.generator.tasks.TileTask;

import tools.jackson.databind.JsonNode;

public class RenderFacadeTaskParams extends TileTaskParams {
    /**
     * A place where to put references (optional, ignored).
     */
    public JsonNode references;
    /**
     * Type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    @JsonProperty("build")
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<LayoutBuilderParams> builders;

    @JsonSetter(nulls = Nulls.FAIL)
    public String height;

    @JsonSetter(nulls = Nulls.FAIL)
    public String altitude;

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
            builders.stream().map(builder -> builder.createBuilder(generation.seed())).collect(Collectors.toList()),
            height,
            altitude
        );
    }
}

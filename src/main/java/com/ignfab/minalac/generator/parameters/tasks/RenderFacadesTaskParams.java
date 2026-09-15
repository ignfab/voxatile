package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.layouts.LayoutBuilderParams;
import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;
import com.ignfab.minalac.generator.tasks.RenderFacadesTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a {@link RenderFacadesTask}.
 */
public class RenderFacadesTaskParams extends ModelTaskParams {
    /**
     * List of builders to try out.
     * <p>
     * Builders will be tried in list order.
     * If a builder fails, next one is tried.
     * If it succeeds, facade is built and next builders won't be used.
     * If all builders fail, nothing will be built.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<LayoutBuilderParams> build;

    /**
     * Name of metadata containing building height (from wall bottom to wall top).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String height;

    /**
     * Name of metadata containing building altitude (altitude of wall bottom).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String altitude;

    /**
     * Creates a new {@code RenderFacadeTaskParams} out of mandatory parameters.
     * @param build builders to try, in order
     * @param height name of metadata containing building height
     * @param altitude name of metadata containing building altitude
     */
    @ConstructorProperties({ "build", "height", "altitude"})
    public RenderFacadesTaskParams(
        List<LayoutBuilderParams> build,
        String height,
        String altitude
    ) {
        this.build = build;
        this.height = height;
        this.altitude = altitude;
    }

    @Override
    public void validate() {
        super.validate();
        if (height.isBlank())
            throw new IllegalArgumentException("Height metadata cannot be blank");
        if (altitude.isBlank())
            throw new IllegalArgumentException("Altitude metadata cannot be blank");
        build.forEach(LayoutBuilderParams::validate);
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderFacadesTask(
            models.create(generation),
            build.stream().map(builder -> {
                try {
                    return builder.createBuilder(generation.seed(), new LayoutBuilderParams.AxesPolicies(
                        LayoutBuilderParams.AxisPolicy.ADJUST,
                        LayoutBuilderParams.AxisPolicy.KEEP,
                        LayoutBuilderParams.AxisPolicy.ADJUST
                    ));
                } catch (UnbuildableLayoutException e) {
                    throw new IllegalArgumentException(e);
                }
            }).toList(),
            height,
            altitude
        );
    }
}

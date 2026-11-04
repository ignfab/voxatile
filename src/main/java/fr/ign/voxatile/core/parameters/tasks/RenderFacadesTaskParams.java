package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.models.values.ModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.layouts.LayoutBuilderParams;
import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.tasks.RenderFacadesTask;
import fr.ign.voxatile.core.tasks.TileTask;

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
     * Building height (from wall bottom to wall top).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams height;

    /**
     * Building base altitude (altitude of wall bottom).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams altitude;

    /**
     * Creates a new {@code RenderFacadesTaskParams} out of mandatory parameters.
     * @param build builders to try, in order
     * @param height building height
     * @param altitude building base altitude (altitude of walls bottom)
     */
    @ConstructorProperties({ "build", "height", "altitude"})
    public RenderFacadesTaskParams(
        List<LayoutBuilderParams> build,
        ModelValueParams height,
        ModelValueParams altitude
    ) {
        this.build = build;
        this.height = height;
        this.altitude = altitude;
    }

    @Override
    public void validate() {
        super.validate();
        height.validate();
        altitude.validate();
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
            height.create(generation),
            altitude.create(generation)
        );
    }
}

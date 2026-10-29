package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.values.FixedValue;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.models.values.ModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.parameters.placeables.layouts.LayoutBuilderParams;
import fr.ign.voxatile.core.placeables.layouts.StructureLayoutBuilder;
import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.placeables.PlaceableStructure;
import fr.ign.voxatile.core.placeables.layouts.LayoutBuilder;
import fr.ign.voxatile.core.tasks.RenderLines2dTask;
import fr.ign.voxatile.core.tasks.TileTask;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

/**
 * Parameters for {@link RenderLines2dTask}.
 */
public class RenderLines2dTaskParams extends ModelTaskParams {
    /**
     * Width of lines to render (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelValueParams width = null;

    /**
     * What to place on line (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableParams place = null;

    /**
     * Layout to build along line (optional).
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<LayoutBuilderParams> build;

    /**
     * Heightmap on which draw lines (from which Z is taken according to X and Y).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap heightmap on which draw lines (from which Z is taken according to X and Y)
     */
    @ConstructorProperties({"heightmap"})
    public RenderLines2dTaskParams(ReadableHeightmapParams heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();

        if (width != null)
            width.validate();

        if (place != null && build != null)
            throw new IllegalArgumentException("Can not have both place and structure fields");

        if (place == null && build == null)
            throw new IllegalArgumentException("There must be a place or a structure field");

        if (build != null)
            build.forEach(LayoutBuilderParams::validate);

        if (place != null)
            place.validate();

        heightmap.validate();
    }

    @Override
    public TileTask create(Generation generation) {

        ModelValue widthValue = null;
        if (width != null)
            widthValue = width.create(generation);

        List<LayoutBuilder> builders;

        if (build == null) {
            if (width == null)
                widthValue = new FixedValue(1.0);
            try {
                builders = Collections.singletonList(new StructureLayoutBuilder(
                    PlaceableStructure.builder().set(WorldBBox3d.ORIGIN, place.create(generation.seed())).build(),
                    new StructureLayoutBuilder.StretchAxis(0),
                    new StructureLayoutBuilder.StretchAxis(0),
                    new StructureLayoutBuilder.StretchAxis(0)
                ));
            } catch (UnbuildableLayoutException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            LayoutBuilderParams.AxisPolicy yPolicy = width == null ? LayoutBuilderParams.AxisPolicy.KEEP : LayoutBuilderParams.AxisPolicy.ADJUST;

            builders = build.stream().map(builder -> {
                try {
                    return builder.createBuilder(generation.seed(), new LayoutBuilderParams.AxesPolicies(
                        LayoutBuilderParams.AxisPolicy.ADJUST,
                        yPolicy,
                        LayoutBuilderParams.AxisPolicy.KEEP
                    ));
                } catch (UnbuildableLayoutException e) {
                    throw new IllegalArgumentException(e);
                }
            }).toList();
        }

        return new RenderLines2dTask(
            models.create(generation),
            builders,
            heightmap.create(generation.heightmaps()),
            widthValue
        );
    }
}


package fr.ign.voxatile.core.parameters.placeables.layouts;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.placeables.PlaceableStructure;
import fr.ign.voxatile.core.placeables.layouts.LayoutBuilder;
import fr.ign.voxatile.core.placeables.layouts.StructureLayoutBuilder;
import fr.ign.voxatile.core.placeables.layouts.StructureLayoutBuilder.StretchAxis;
import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.utils.random.Seed;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;

/**
 * This params creates a {@link PlaceableStructure} made of one placeable stretcheable in every direction.
 */
public class FillWithLayoutBuilderParams implements LayoutBuilderParams {
    /**
     * What to fill with. This will be placed at each position to fill.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams fillWith;

    /**
     * Offset of the placement along x-axis.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int xOffset = 0;

    /**
     * Offset of the placement along y-axis.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int yOffset = 0;

    /**
     * Offset of the placement along z-axis.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int zOffset = 0;

    @Override
    public void validate() {
        fillWith.validate();
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed, AxesPolicies policies) throws UnbuildableLayoutException {
        PlaceableStructure.Builder builder = new PlaceableStructure.Builder();
        builder.set(new WorldCoords3d(xOffset, yOffset, zOffset), fillWith.create(seed));

        return new StructureLayoutBuilder(
            builder.build(),
            new StretchAxis(xOffset, 1, policies.x() == AxisPolicy.ADJUST ? Integer.MAX_VALUE : 1),
            new StretchAxis(yOffset, 1, policies.y() == AxisPolicy.ADJUST ? Integer.MAX_VALUE : 1),
            new StretchAxis(zOffset, 1, policies.z() == AxisPolicy.ADJUST ? Integer.MAX_VALUE : 1)
        );
    }

}

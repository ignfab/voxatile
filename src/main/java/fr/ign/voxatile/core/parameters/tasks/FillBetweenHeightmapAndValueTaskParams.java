package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.models.values.ModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.NothingParams;
import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.tasks.FillBetweenHeightmapAndValueTask;
import fr.ign.voxatile.core.tasks.TileTask;

/**
 * Parameters for creating a {@link FillBetweenHeightmapAndValueTask}.
 */
public class FillBetweenHeightmapAndValueTaskParams extends ModelTaskParams {
    /**
     * {@code ReadableHeightmap} to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Model value to use as altitude (required).
     */
    public ModelValueParams altitudeValue;

    /**
     * {@code Placeable} placed above the altitude value (optional).
     */
    public PlaceableParams placeAbove = new NothingParams();

    /**
     * {@code Placeable} placed below the altitude value (optional).
     */
    public PlaceableParams placeBelow = new NothingParams();

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param heightmap {@code ReadableHeightmap} to use
     * @param altitudeValue model value to use as altitude
     */
    @ConstructorProperties({ "heightmap", "altitudeValue" })
    public FillBetweenHeightmapAndValueTaskParams(
        ReadableHeightmapParams heightmap,
        ModelValueParams altitudeValue
    ) {
        this.heightmap = heightmap;
        this.altitudeValue = altitudeValue;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
        placeAbove.validate();
        placeBelow.validate();

        if (placeAbove instanceof NothingParams && placeBelow instanceof NothingParams)
            throw new IllegalArgumentException("At least the 'placeAbove' or 'placeBelow' field must be specified.");
        altitudeValue.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new FillBetweenHeightmapAndValueTask(
            models.create(generation),
            heightmap.create(generation.heightmaps()),
            altitudeValue.create(generation),
            placeAbove.create(generation.seed()),
            placeBelow.create(generation.seed())
        );
    }
}

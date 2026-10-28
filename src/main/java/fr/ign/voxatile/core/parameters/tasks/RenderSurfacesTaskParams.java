package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.ConstantHeightmapParams;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.models.values.FixedValueParams;
import fr.ign.voxatile.core.parameters.models.values.ModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.tasks.RenderSurfacesTask;
import fr.ign.voxatile.core.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderSurfacesTask}.
 */
public class RenderSurfacesTaskParams extends ModelTaskParams {
    /**
     * Heightmap to render on (optional default 0).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams heightmap = new ConstantHeightmapParams(0);

    /**
     * Vertivcal offset to add (optional default 0).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelValueParams altitudeOffset = new FixedValueParams(0.0);

    /**
     * What to place on surface (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place what to place on surface.
     */
    @ConstructorProperties({ "place"})
    public RenderSurfacesTaskParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
        altitudeOffset.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderSurfacesTask(
            models.create(generation),
            heightmap.create(generation.heightmaps()),
            altitudeOffset.create(generation),
            place.create(generation.seed())
        );
    }
}

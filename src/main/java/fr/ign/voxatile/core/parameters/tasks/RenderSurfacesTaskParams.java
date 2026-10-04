package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.tasks.RenderSurfacesTask;
import fr.ign.voxatile.core.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderSurfacesTask}.
 */
public class RenderSurfacesTaskParams extends ModelTaskParams {
    /**
     * Heightmap to render on (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;
    /**
     * What to place on surface (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap heightmap to render on.
     * @param place what to place on surface.
     */
    @ConstructorProperties({ "heightmap", "place"})
    public RenderSurfacesTaskParams(ReadableHeightmapParams heightmap, PlaceableParams place) {
        this.heightmap = heightmap;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderSurfacesTask(
            models.create(generation),
            heightmap.create(generation.heightmaps()),
            place.create(generation.seed())
        );
    }
}

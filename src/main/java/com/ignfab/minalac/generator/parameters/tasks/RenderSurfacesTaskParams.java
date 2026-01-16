package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderSurfacesTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderSurfacesTask}.
 */
public class RenderSurfacesTaskParams extends ModelTaskParams {
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams heightmap;

    @JsonSetter(nulls = Nulls.SKIP)
    public ModelValueParams altitude;
    /**
     * What to place on surfaces (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place what to place on surface.
     */
    @ConstructorProperties({"place"})
    public RenderSurfacesTaskParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        if ((heightmap == null) == (altitude == null))
            throw new IllegalArgumentException("One (and only one) of 'heightmap' and 'altitude' must be specified");
        models.validate();
        if (heightmap != null)
            heightmap.validate();
        if (altitude != null)
            altitude.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        RenderSurfacesTask.GetZ atZ;
        if (heightmap != null) {
            ReadableHeightmapSpec heightmapSpec = heightmap.create(generation.heightmaps());
            atZ = (tile, model, coords) -> tile.heightmaps().get(heightmapSpec).get(coords);
        } else {
            ModelValue altitudeValue = altitude.create(generation);
            atZ = (tile, model, coords) -> altitudeValue.getAsInt(model).orElseThrow(() -> new IgnorableException("Missing altitude value"));
        }
        return new RenderSurfacesTask(
            models.create(),
            atZ,
            place.create(generation.seed())
        );
    }
}

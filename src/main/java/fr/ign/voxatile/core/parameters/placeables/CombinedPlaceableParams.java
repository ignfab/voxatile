package fr.ign.voxatile.core.parameters.placeables;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.parameters.JsonWrapper;
import fr.ign.voxatile.core.placeables.CombinedPlaceable;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.random.Seed;

/**
 * Parameters for a {@link CombinedPlaceable}.
 * CombinedPlaceable is actually represented by a list of placeables in parameter file.
 */
@JsonWrapper
public class CombinedPlaceableParams extends PlaceableParams {
    /**
     * Contained placeable params.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public List<PlaceableParams> placeableParams;

    @Override
    public void validate() {
        for (PlaceableParams placeable : placeableParams)
            placeable.validate();
    }

    @Override
    public Placeable create(Seed seed) {
        CombinedPlaceable combined = new CombinedPlaceable();
        for (PlaceableParams placeable : placeableParams)
            combined.add(placeable.create(seed));

        return combined;
    }
}

package fr.ign.voxatile.core.parameters.placeables;

import fr.ign.voxatile.core.placeables.Nothing;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.random.Seed;

/**
 * Places nothing.
 */
public class NothingParams extends PlaceableParams {
    @Override
    public Placeable create(Seed seed) {
        return Nothing.INSTANCE;
    }
}

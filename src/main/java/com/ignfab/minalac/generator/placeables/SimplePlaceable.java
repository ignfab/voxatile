package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * A {@code SimplePlaceable} is a {@link Placeable} that is its own {@link Placer}. I.E. a placeable that needs no context.
 */
public interface SimplePlaceable extends Placeable, Placer {

    @Override
    default Placer placer(Seed seed, Model model) {
        return this;
    }
}

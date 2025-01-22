package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * A placeable that places a placeable with a random chance.
 */
public class RandomPattern implements Placeable {

    private final Placeable placeable;
    private final double chance;

    /**
     * Creats a new {@code RandomPattern}.
     *
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    public RandomPattern(Placeable placeable, double chance) {
        this.placeable = placeable;
        this.chance = chance;
    }

    @Override
    public Placer placer(Seed seed, Model model) {
        // Re-salting here ensures several patterns on same model wont use the same seed.
        return new RandomPatternPlacer(seed.salt(this.toString()), placeable.placer(seed, model), chance);
    }

}

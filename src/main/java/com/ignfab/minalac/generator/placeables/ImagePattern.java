package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A pattern placing something or not according to a simple "dice roll".
 */
public class ImagePattern implements Pattern {
    private final ModelSelection models;

    /**
     * Creates a new {@code RandomPattern}.
     *
     * @param seed {@link Seed} to use for random number generation
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    public ImagePattern(ModelSelection models) {
        this.models = models;
    }

    @Override
    public Placeable get(GenerationTile tile, int x, int y, int z) {

        // TODO: for CPU optimization sake, forTile should cache selection by tile
        for (Model model: models.forTile(tile))
            if (model instanceof IntegerMatrixModel matrix) {
                Integer value = matrix.get(new WorldCoords2d(x, y));
                if (value != null) {
                    int r = value >>> 16 & 0xFF;
                    int g = value >>> 8 & 0xFF;
                    int b = value & 0xFF;
                }

            }



        return Nothing.INSTANCE;
    }
}

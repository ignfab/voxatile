package com.ignfab.minalac.generator.placeables;

import java.util.Map;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.Color;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A pattern placing something or not according to a simple "dice roll".
 */
public class ImagePattern implements Pattern {
    private final ModelSelection models;

    // TODO: Weights could be associated to colors
    private final Map<Color, Placeable> placeables;

    /**
     * Creates a new {@code RandomPattern}.
     *
     * @param seed {@link Seed} to use for random number generation
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    public ImagePattern(ModelSelection models, Map<Color, Placeable> placeables) {
        this.models = models;
        this.placeables = placeables;
    }

    @Override
    public Placeable get(GenerationTile tile, int x, int y, int z) {

        Placeable placeable = Nothing.INSTANCE;
        double distance = Double.MAX_VALUE;

        // TODO: for CPU optimization sake, forTile should cache selection by tile
        for (Model model: models.forTile(tile))
            if (model instanceof IntegerMatrixModel matrix) {
                Integer value = matrix.get(new WorldCoords2d(x, y));
                if (value != null) {
                    Color color = Color.fromRGBint(value);
                    for (Map.Entry<Color, Placeable> candidate : placeables.entrySet()) {
                        double d = color.distance(candidate.getKey());
                        if (d < distance) {
                            distance = d;
                            placeable = candidate.getValue();
                        }
                    }
                }

            }

        return placeable;
    }
}

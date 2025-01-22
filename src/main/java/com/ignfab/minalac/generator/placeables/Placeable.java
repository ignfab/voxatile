package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * The {@code Placeable} interface represents something placeable in voxel world.
 * @see VoxelType
 */
public interface Placeable {
    /**
     * Instanciates a {@link Placer} for this {@code Placeable} with a given {@link Seed} and for a given {@link Model}.
     *
     * @param seed Random seed to use if needed
     * @param model Model corresponding to this placement
     *
     * @return a {@link Placer} for this {@code Placeable}
     */
    Placer placer(Seed seed, Model model);
}

package com.ignfab.minalac.generator.placeables;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A compound placeable is a placeable composed of other placeables.
 * It automatically computes the {@link #palette()} from the
 * {@link #components()}.
 */
public interface CompoundPlaceable extends Placeable {
    /**
     * {@return the other placeables composing this placeable}
     */
    Collection<Placeable> components();

    @Override
    default Set<Placeable> palette() {
        Collection<Placeable> placeables = components();
        if (placeables.isEmpty())
            return Set.of();
        if (placeables.size() == 1)
            return placeables.iterator().next().palette();
        Set<Placeable> palette = new HashSet<>();
        for (Placeable placeable : placeables)
            palette.addAll(placeable.palette());
        return palette;
    }
}

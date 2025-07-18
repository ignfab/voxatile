package com.ignfab.minalac.generator.placeables.gettable2d;

import com.ignfab.minalac.generator.placeables.Placeable;

/**
 * The {@code Gettable2d} represents something that can return a {@link Placeable} in two-dimension.
 */
public interface Gettable2d {

    // TODO-PR: Find better names for first and second axis coordinates. (i, j) is usually used for-loop and (u, v) is too mathematics.
    /**
     * Returns the corresponding placeable.
     *
     * @param u position u-coordinate
     * @param v position v-coordinate
     * @return the {@link Placeable} at given coordinates
     */
    Placeable get(int u, int v);
}

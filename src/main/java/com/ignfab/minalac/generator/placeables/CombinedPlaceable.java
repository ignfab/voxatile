package com.ignfab.minalac.generator.placeables;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A placeable that is a combination of placeables.
 * Placeable will be placed in the order they were given.
 * This object itself is immutable, but can be created with the {@link #builder()}.
 */
public final class CombinedPlaceable implements CompoundPlaceable {
    private final List<Placeable> placeables;

    /**
     * Reusable empty combined placeable instance.
     * Note that there are not much cases where this is useful (mostly unit tests).
     * You should consider using {@link Nothing#INSTANCE} for similar behavior instead!
     */
    public static final CombinedPlaceable EMPTY = new CombinedPlaceable();

    private CombinedPlaceable() {
        placeables = List.of();
    }

    private CombinedPlaceable(List<Placeable> placeables) {
        this.placeables = List.copyOf(placeables);
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        placeables.forEach((placeable) -> placeable.place(tile, x, y, z));
    }

    @Override
    public Collection<Placeable> components() {
        return placeables;
    }

    /**
     * Creates a new builder from this combined placeable.
     * @return a pre-filled builder with this placeable's content
     */
    public Builder toBuilder() {
        return builder().addAll(this.placeables);
    }

    /**
     * Creates a new builder.
     * @return an empty builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Simple builder to create combined placeables from multiple placeables.
     */
    public static final class Builder {
        private final List<Placeable> placeables = new LinkedList<>();

        /**
         * Adds a placeable at the end of the placeables list.
         *
         * @param placeable Placeable to add
         * @return {@code this}
         */
        public Builder add(Placeable placeable) {
            placeables.add(placeable);
            return this;
        }

        /**
         * Adds placeables in order at the end of the placeables list.
         *
         * @param placeables Placeables to add
         * @return {@code this}
         */
        public Builder addAll(Collection<? extends Placeable> placeables) {
            this.placeables.addAll(placeables);
            return this;
        }

        /**
         * Creates an immutable combined placeable from this builder.
         * @return the created placeable
         */
        public CombinedPlaceable build() {
            return placeables.isEmpty() ? EMPTY : new CombinedPlaceable(placeables);
        }
    }
}

package com.ignfab.minalac.generator.placeables;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code PlaceableStructure} is a {@link Placeable} consisting of placeables at given coordinate offsets.
 * The structure itself is immutable, but can be defined by using the {@link #builder()}.
 */
public final class PlaceableStructure implements Placeable {
    private final Map<WorldCoords3d, Placeable> placeables;
    private final WorldBBox3d limits;
    private final WorldBBox3d bbox;

    /**
     * Reusable empty structure instance.
     */
    public static final PlaceableStructure EMPTY = new PlaceableStructure();

    private PlaceableStructure() {
        placeables = Map.of();
        limits = WorldBBox3d.EMPTY;
        bbox = WorldBBox3d.EMPTY;
    }

    private PlaceableStructure(Map<WorldCoords3d, Placeable> placeables) {
        this.placeables = Map.copyOf(placeables);
        limits = new WorldBBox3d(this.placeables.keySet().toArray(WorldCoords3d[]::new));
        bbox = WorldBBox3d.surrounding(() ->
            this.placeables.entrySet().stream().map(
                (Map.Entry<WorldCoords3d, Placeable> e) -> (Bounded3d) e.getValue().bbox().shift(e.getKey())
            ).iterator()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        for (Map.Entry<WorldCoords3d, Placeable> entry : placeables.entrySet()) {
            WorldCoords3d c = entry.getKey();
            entry.getValue().place(tile, c.x() + x, c.y() + y, c.z() + z);
        }
    }

    /**
     * Returns the placeable at the specified coordinates.
     *
     * @param coords structure relative coordinates
     *
     * @return placeable at relative structure coordinates or {@link Nothing#INSTANCE} if none
     */
    public Placeable get(WorldCoords3d coords) {
        return placeables.getOrDefault(coords, Nothing.INSTANCE);
    }

    /**
     * Returns the placeable at the specified coordinates.
     *
     * @param x structure relative x-coordinate
     * @param y structure relative y-coordinate
     * @param z structure relative z-coordinate
     *
     * @return placeable at relative structure coordinates or {@link Nothing#INSTANCE} if none
     */
    public Placeable get(int x, int y, int z) {
        return get(new WorldCoords3d(x, y, z));
    }

    /**
     * {@return the limits of this structure in relative coordinates}
     * <p>
     * This is not the bounding box of all that would be placed.
     * Limits will only contain origin coordinates of contained placeables.
     * <p>
     * In other words, limits is the smallest bounding box containing every position
     * for which {@link #get} returns something other than {@link Nothing#INSTANCE}.
     * This may be used to know how to repeat this structure.
     */
    public WorldBBox3d limits() {
        return limits;
    }

    /**
     * Creates a new builder from this structure.
     * @return a pre-filled builder with this structure's content
     */
    public Builder toBuilder() {
        return builder().merge(0, 0, 0, this);
    }

    /**
     * Creates a new builder.
     * @return an empty builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    /**
     * Simple builder to create structures from methods such as
     * {@link #set(WorldCoords3d, Placeable)} and {@link #remove(WorldCoords3d)}.
     */
    public static final class Builder {
        private final Map<WorldCoords3d, Placeable> placeables = new HashMap<>();

        /**
         * Adds, replaces or removes a placeable at the specified coordinates.
         * If the provided placeable value is {@code null}, any placeable at the specified coordinates will be removed.
         * Coordinates are relative.
         *
         * @param coords the relative {@code WorldCoords3d}
         * @param placeable the placeable to be added or {@code null} value
         * @return {@code this}
         */
        public Builder set(WorldCoords3d coords, Placeable placeable) {
            if (placeable == null)
                placeables.remove(coords);
            else
                placeables.put(coords, placeable);
            return this;
        }

        /**
         * Adds, replaces or removes a placeable at the specified coordinates.
         * If the provided placeable is {@code null}, any placeable at the specified coordinates will be removed.
         * Coordinates are relative.
         *
         * @param x the x-coordinate value
         * @param y the y-coordinate value
         * @param z the z-coordinate value
         * @param placeable the placeable to be added or {@code null} value
         * @return {@code this}
         */
        public Builder set(int x, int y, int z, Placeable placeable) {
            return set(new WorldCoords3d(x, y, z), placeable);
        }

        /**
         * Adds, replaces or removes placeables at all the coordinates within the specified bounding box.
         * If the provided placeable is {@code null}, any placeable within the bounding box will be removed.
         *
         * @param bbox  the bounding box
         * @param placeable the placeable to be added or {@code null} value
         * @return {@code this}
         */
        public Builder set(WorldBBox3d bbox, Placeable placeable) {
            for (WorldCoords3d coords : bbox)
                set(coords, placeable);
            return this;
        }

        /**
         * Removes the placeable, if it exists, at the specified coordinates.
         *
         * @param coords the relative {@code WorldCoords3d}
         * @return {@code this}
         */
        public Builder remove(WorldCoords3d coords) {
            return set(coords, null);
        }

        /**
         * Removes the placeable, if it exists, at the specified coordinates.
         *
         * @param x the x-coordinate value
         * @param y the y-coordinate value
         * @param z the z-coordinate value
         * @return {@code this}
         */
        public Builder remove(int x, int y, int z) {
            return set(x, y, z, null);
        }

        /**
         * Removes all placeables within the provided BBOX.
         *
         * @param bbox the bounding box
         * @return {@code this}
         */
        public Builder remove(WorldBBox3d bbox) {
            return set(bbox, null);
        }

        /**
         * Merges the given structure into this builder, with an offset.
         * This will take all placeables of the structure and set them with
         * the given offset added to their initial offset, potentially
         * overwriting existing placeables of this builder.
         * @param x the x-coordinate value
         * @param y the y-coordinate value
         * @param z the z-coordinate value
         * @param structure the structure to merge
         * @return {@code this}
         */
        public Builder merge(int x, int y, int z, PlaceableStructure structure) {
            if (x == 0 && y == 0 && z == 0)
                placeables.putAll(structure.placeables);
            else
                for (Map.Entry<WorldCoords3d, Placeable> entry : structure.placeables.entrySet())
                    set(entry.getKey().add(x, y, z), entry.getValue());
            return this;
        }

        /**
         * Merges the given structure into this builder, with an offset.
         * This will take all placeables of the structure and set them with
         * the given offset added to their initial offset, potentially
         * overwriting existing placeables of this builder.
         * @param coords the relative {@code WorldCoords3d}
         * @param structure the structure to merge
         * @return {@code this}
         */
        public Builder merge(WorldCoords3d coords, PlaceableStructure structure) {
            return merge(coords.x(), coords.y(), coords.z(), structure);
        }

        /**
         * Creates an immutable structure from this builder.
         * @return the created structure
         */
        public PlaceableStructure build() {
            return placeables.isEmpty() ? EMPTY : new PlaceableStructure(placeables);
        }
    }
}

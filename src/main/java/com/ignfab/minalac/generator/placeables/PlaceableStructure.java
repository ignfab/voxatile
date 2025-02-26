package com.ignfab.minalac.generator.placeables;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * {@code PlaceableStructure} is a {@link Placeable} consisting of placeables at given coordinate offsets.
 * The structure can be defined notably by using the methods {@link #set(WorldCoords3d, Placeable)} and {@link #remove(WorldCoords3d)}.
 */
public class PlaceableStructure implements Placeable {
    private final Map<WorldCoords3d, Placeable> placeables = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(int x, int y, int z) {
        placeables.forEach((c, placeable) -> placeable.place(c.x() + x, c.y() + y, c.z() + z));
    }

    /**
     * Adds, replaces or removes a placeable at the specified coordinates.
     * If the provided placeable value is {@code null}, any placeable at the specified coordinates will be removed.
     * Coordinates are relative.
     *
     * @param coords the relative {@code WorldCoords3d}
     * @param placeable the placeable to be added or {@code null} value
     */
    public void set(WorldCoords3d coords, Placeable placeable) {
        if (placeable == null)
            remove(coords);
        else
            placeables.put(coords, placeable);
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
     */
    public void set(int x, int y, int z, Placeable placeable) {
        set(new WorldCoords3d(x, y, z), placeable);
    }

    /**
     * Adds, replaces or removes placeables at all the coordinates within the specified bounding box.
     * If the provided placeable is {@code null}, any placeable within the bounding box will be removed.
     *
     * @param bbox  the bounding box
     * @param placeable the placeable to be added or {@code null} value
     */
    public void set(WorldBBox3d bbox, Placeable placeable) {
        if (placeable == null)
            remove(bbox);
        else
            for (WorldCoords3d coords : bbox)
                set(coords, placeable);
    }

    /**
     * Removes the placeable, if it exists, at the specified coordinates.
     *
     * @param coords the relative {@code WorldCoords3d}
     */
    public void remove(WorldCoords3d coords) {
        placeables.remove(coords);
    }

    /**
     * Removes the placeable, if it exists, at the specified coordinates.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     */
    public void remove(int x, int y, int z) {
        remove(new WorldCoords3d(x, y, z));
    }

    /**
     * Removes all placeables within the provided BBOX.
     *
     * @param bbox the bounding box
     */
    public void remove(WorldBBox3d bbox) {
        for (WorldCoords3d coords : bbox)
            remove(coords);
    }
}

package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.Structure3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code SimpleVoxelStructure} is a {@link Placeable} consisting of placeables at given coordinate offsets.
 * The structure can be defined notably by using the methods {@link #set(WorldCoords3d, Placeable)} and {@link #remove(WorldCoords3d)}.
 */
public class PlaceableStructure implements Placeable, Structure3d {
    private final Map<WorldCoords3d, Placeable> placeables = new HashMap<>();
    private WorldBBox3d bbox;

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
        bbox = null; // Force bbox recompute
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
        bbox = null; // Force bbox recompute
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

    private void computeBbox() {
        if (placeables.isEmpty()) {
            bbox = WorldBBox3d.EMPTY;
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (WorldCoords3d position : placeables.keySet()) {
            minX = Math.min(minX, position.x());
            minY = Math.min(minY, position.y());
            minZ = Math.min(minZ, position.z());
            maxX = Math.max(maxX, position.x());
            maxY = Math.max(maxY, position.y());
            maxZ = Math.max(maxZ, position.z());
        }
        bbox = new WorldBBox3d(minX, minY, minZ,
            maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
    }

    @Override
    public WorldBBox3d bbox() {
        if (bbox == null)
            computeBbox();
        return bbox;
    }

    @Override
    public Placeable get(WorldCoords3d position) {
        return placeables.get(position);
    }
}





package com.ignfab.minalac.generator.placeables;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code PlaceableStructure} is a {@link Placeable} consisting of placeables at given coordinate offsets.
 * The structure can be defined notably by using the methods {@link #set(WorldCoords3d, Placeable)} and {@link #remove(WorldCoords3d)}.
 */
public class PlaceableStructure implements Placeable {
    private final Map<WorldCoords3d, Placeable> placeables = new HashMap<>();
    private WorldBBox3d limits = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        placeables.forEach((c, placeable) -> placeable.place(tile, c.x() + x, c.y() + y, c.z() + z));
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
            placeables.remove(coords);
        else
            placeables.put(coords, placeable);

        // Force limits recompute
        limits = null;
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
        set(coords, null);
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

    /**
     * Returns the placeable at the specified coordinates.
     *
     * @param x relative x-coordinate
     * @param y relative y-coordinate
     * @param z relative z-coordinate
     *
     * @return placeable at relative structure coordinates or {@code NoVoxel.INSTANCE} if none
     */
    public Placeable get(int x, int y, int z) {
        Placeable placeable = placeables.get(new WorldCoords3d(x, y, z));
        return placeable == null ? Nothing.INSTANCE : placeable;
    }

    /**
     * Tells the limits of this structure in relative coordinates.
     * <p>
     * This is not the bounding box of all that would be placed.
     * Limits will only contain origin coordinates of contained placeables.
     * <p>
     * In other words, limits contains every position for which {@link #get} returns something other than {@link Nothing#INSTANCE}.
     * This may be used to know how to repeat this structure.
     *
     * @return limits of the structure in relative coordinates.
     */
    public WorldBBox3d limits() {
        if (limits == null)
            limits = placeables.isEmpty() ? WorldBBox3d.EMPTY
                : new WorldBBox3d(placeables.keySet().toArray(new WorldCoords3d[0]));

        return limits;
    }
}

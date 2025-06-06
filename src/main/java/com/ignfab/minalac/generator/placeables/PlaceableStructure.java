package com.ignfab.minalac.generator.placeables;

import java.util.Map;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code PlaceableStructure} is a {@link Placeable} consisting of placeables at given coordinate offsets.
 */
public class PlaceableStructure implements Placeable {
    private final Map<WorldCoords3d, Placeable> placeables;
    private final WorldBBox3d limits;

    /**
     * Create a new {@code PlaceableStructure} from a mapping.
     * Each key and value must be not null.
     *
     * @param placeables the mapping associating position with placeable.
     */
    public PlaceableStructure(Map<WorldCoords3d, Placeable> placeables) {
        this.placeables = placeables;
        limits = placeables.isEmpty() ? WorldBBox3d.EMPTY : new WorldBBox3d(placeables.keySet().toArray(new WorldCoords3d[0]));
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
        return limits;
    }
}

package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code SimpleVoxelStructure} is a {@link Placeable} consisting of voxels at given coordinate offsets.
 * The pattern can be defined notably by using the methods {@link #set(WorldCoords3d, VoxelType)} and {@link #remove(WorldCoords3d)}.
 */
public class SimpleVoxelStructure implements Placeable {
    private final Map<WorldCoords3d, Placeable> voxels = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void place(int x, int y, int z) {
        voxels.forEach((c, voxel) -> voxel.place(c.x() + x, c.y() + y, c.z() + z));
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
            voxels.put(coords, placeable);
    }

    /**
     * Adds, replaces or removes a voxel at the specified coordinates.
     * If the provided voxel value is {@code null}, any voxel at the specified coordinates will be removed.
     * Coordinates are relative.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     * @param placeable the voxel to be added or {@code null} value
     */
    public void set(int x, int y, int z, Placeable placeable) {
        set(new WorldCoords3d(x, y, z), placeable);
    }

    /**
     * Adds, replaces or removes voxels at all the coordinates within the specified bounding box.
     * If the provided voxel value is {@code null}, any voxel within the bounding box will be removed.
     *
     * @param bbox  the bounding box
     * @param placeable the voxel to be added or {@code null} value
     */
    public void set(WorldBBox3d bbox, Placeable placeable) {
        if (placeable == null)
            remove(bbox);
        else
            for (WorldCoords3d coords : bbox)
                set(coords, placeable);
    }

    /**
     * Removes the voxel, if it exists, at the specified coordinates.
     *
     * @param coords the relative {@code WorldCoords3d}
     */
    public void remove(WorldCoords3d coords) {
        voxels.remove(coords);
    }

    /**
     * Removes the voxel, if it exists, at the specified coordinates.
     *
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     * @param z the z-coordinate value
     */
    public void remove(int x, int y, int z) {
        remove(new WorldCoords3d(x, y, z));
    }

    /**
     * Removes all voxels within the provided BBOX.
     *
     * @param bbox the bounding box
     */
    public void remove(WorldBBox3d bbox) {
        for (WorldCoords3d coords : bbox)
            remove(coords);
    }
}

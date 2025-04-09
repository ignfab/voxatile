package com.ignfab.minalac.generator.world;

import java.util.Iterator;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A descending iterator over the voxels and associated coordinates of a tile's column.
 * It is the default iterator of {@link VoxelTile} and, as such, it is not optimized for its implementations.
 */
public class VoxelColumnIterator implements Iterator<PlacedVoxel> {
    private final VoxelTile tile;
    private final int x;
    private final int y;
    private final int zMin;
    private int currentZ;
    private Placeable currentVoxel;

    /**
     * Constructs a new {@code VoxelColumnIterator}.
     *
     * @param tile the tile where voxels are
     * @param x x-coordinate of the column to iterate over
     * @param y y-coordinate of the column to iterate over
     * @param zMin z-coordinate of the lowest column voxel
     * @param zMax z-coordinate of the highest column voxel
     */
    public VoxelColumnIterator(VoxelTile tile, int x, int y, int zMin, int zMax) {
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.zMin = zMin;
        currentZ = zMax + 1;
        moveOn();
    }

    private void moveOn() {
        currentVoxel = null;
        while (currentZ > zMin && currentVoxel == null) {
            currentZ--;
            currentVoxel = tile.getVoxel(x, y, currentZ);
        }
    }

    @Override
    public boolean hasNext() {
        return currentVoxel != null;
    }

    @Override
    public PlacedVoxel next() {
        PlacedVoxel next = new PlacedVoxel(currentVoxel, new WorldCoords3d(x, y, currentZ));
        moveOn();
        return next;
    }
}

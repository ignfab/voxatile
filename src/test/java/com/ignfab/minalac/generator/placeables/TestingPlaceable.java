package com.ignfab.minalac.generator.placeables;

import java.util.Set;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class TestingPlaceable implements Placeable {

    private int timesPlaced = 0;
    private WorldCoords3d lastPlaced = null;

    public WorldCoords3d lastPlaced() {
        return lastPlaced;
    }

    public int timesPlaced() {
        return timesPlaced;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        place(tile, new WorldCoords3d(x, y, z));
    }

    @Override
    public void place(VoxelTile tile, WorldCoords3d position) {
        timesPlaced++;
        lastPlaced = position;
    }

    // This behaves like an empty placeable since it does not actually place anything.
    // To help testing the palette, use TestingVoxel, which behaves like an actual voxel.
    @Override
    public Set<Placeable> palette() {
        return Set.of();
    }
}

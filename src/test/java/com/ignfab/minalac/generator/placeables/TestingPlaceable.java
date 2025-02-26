package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

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
    public void place(int x, int y, int z) {
        timesPlaced++;
        lastPlaced = new WorldCoords3d(x, y, z);
    }
}

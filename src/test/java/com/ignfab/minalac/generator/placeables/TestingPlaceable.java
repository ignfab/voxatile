package com.ignfab.minalac.generator.placeables;

import java.util.Random;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class TestingPlaceable implements Placeable {

    private WorldCoords3d lastPlaced = null;

    WorldCoords3d lastPlaced() {
        return lastPlaced;
    }

    @Override
    public void place(Random random, int x, int y, int z) {
        lastPlaced = new WorldCoords3d(x, y, z);
    }
}

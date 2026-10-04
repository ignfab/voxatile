package fr.ign.voxatile.core.placeables;

import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;
import fr.ign.voxatile.core.world.VoxelTile;

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
        timesPlaced++;
        lastPlaced = new WorldCoords3d(x, y, z);
    }
}

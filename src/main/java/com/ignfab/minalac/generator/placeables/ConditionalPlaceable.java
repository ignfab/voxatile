package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.world.VoxelTile;

public class ConditionalPlaceable implements Pattern {

    private Placeable placeable;
    private Condition condition;

    public ConditionalPlaceable(Placeable placeable, Condition condition) {
        this.placeable = placeable;
        this.condition = condition;
    }

    @Override
    public Placeable get(VoxelTile tile, int x, int y, int z) {
        return  condition.check(tile, x, y, z) ? placeable : Nothing.INSTANCE;
    }

    public interface Condition {
        boolean check(VoxelTile tile, int x, int y, int z);
    }

}

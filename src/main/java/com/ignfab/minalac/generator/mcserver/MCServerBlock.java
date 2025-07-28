package com.ignfab.minalac.generator.mcserver;

import net.minestom.server.instance.block.Block;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.world.VoxelTile;

public record MCServerBlock(Block block) implements Placeable {
    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        ((MCServerTile) tile).setBlock(x, y, z, this.block);
    }
}

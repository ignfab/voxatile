package com.ignfab.minalac.generator.outputs.hytale;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.world.VoxelTile;

public record HytaleVoxel(int blockId, String blockTypeKey) implements Placeable {
    public HytaleVoxel(String blockTypeKey) {
        this(BlockType.getAssetMap().getIndex(blockTypeKey), blockTypeKey);
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        ((HytaleVoxelTile) tile).setBlock(x, y, z, this);
    }
}

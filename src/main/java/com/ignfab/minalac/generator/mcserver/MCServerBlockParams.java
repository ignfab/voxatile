package com.ignfab.minalac.generator.mcserver;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Objects;

import net.minestom.server.instance.block.Block;

import com.ignfab.minalac.generator.parameters.placeables.voxels.MCVoxelParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

public class MCServerBlockParams extends MCVoxelParams {
    @ConstructorProperties("block")
    public MCServerBlockParams(String block) {
        super(block);
    }

    @Override
    public Placeable create(Seed seed) {
        return new MCServerBlock(Block.fromKey(block).withProperties(Objects.requireNonNullElse(properties, Map.of())));
    }
}

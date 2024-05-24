package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;

public class MCVoxelTypeFactory implements VoxelTypeFactory {
    private final MCVoxelWorld world;

    public MCVoxelTypeFactory(MCVoxelWorld world) {
        this.world = world;
    }

    @Override
    public MCVoxelType createVoxelType(SemanticType semanticType) {
        return new MCVoxelType(world, switch (semanticType) {
            case GRASS -> "minecraft:grass_block";
            case STONE -> "minecraft:stone";
            case AIR -> "minecraft:air";
            case WATER -> "minecraft:water";
            case DIRT -> "minecraft:dirt";
            case COBBLE -> "minecraft:cobblestone";
            case BRICK -> "minecraft:stone_bricks";
        });
    }
}

package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.outputs.minetest.voxelType.MTSimpleVoxelType;

public class MTVoxelTypeFactory implements VoxelTypeFactory {
    private final MTVoxelWorld world;

    public MTVoxelTypeFactory(MTVoxelWorld world) {
        this.world = world;
    }

    @Override
    public VoxelType createVoxelType(SemanticType semanticType) {
        //Node string can be found on https://wiki.minetest.net/Games/Minetest_Game/Nodes
        return switch (semanticType) {
            case GRASS -> new MTSimpleVoxelType(this.world, "default:dirt_with_grass");
            case STONE -> new MTSimpleVoxelType(this.world, "default:stone");
            case DIRT -> new MTSimpleVoxelType(this.world, "default:dirt");
            case COBBLE -> new MTSimpleVoxelType(this.world, "default:cobble");
            case BRICK -> new MTSimpleVoxelType(this.world, "default:stonebrick");
            case WATER -> new MTSimpleVoxelType(this.world, "default:water_source");
            default -> new MTSimpleVoxelType(this.world, "air");
        };
    }
}

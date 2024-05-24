package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.outputs.minetest.voxelType.MTSimpleVoxelType;

public class MTVoxelTypeFactory implements VoxelTypeFactory {
    private MTVoxelWorld world;

    public MTVoxelTypeFactory(MTVoxelWorld world) {
        this.world = world;
    }

    @Override
    public VoxelType createVoxelType(SemanticType semanticType) {
        //Node string can be found on https://wiki.minetest.net/Games/Minetest_Game/Nodes
        switch (semanticType) {
            case GRASS:
                return new MTSimpleVoxelType(this.world, "default:dirt_with_grass");
            case STONE:
                return new MTSimpleVoxelType(this.world, "default:stone");
            case DIRT:
                return new MTSimpleVoxelType(this.world, "default:dirt");
            case COBBLE:
                return new MTSimpleVoxelType(this.world, "default:cobble");
            case BRICK:
                return new MTSimpleVoxelType(this.world, "default:stonebrick");
            case WATER:
                return new MTSimpleVoxelType(this.world, "default:water_source");
            default:
                return new MTSimpleVoxelType(this.world, "air");
        }
    }
}

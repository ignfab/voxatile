package com.ignfab.minalac.generator.outputs.testing;

import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelType;

public class TestingVoxelTypeFactory implements VoxelTypeFactory {
    private TestingVoxelWorld world;

    public TestingVoxelTypeFactory(TestingVoxelWorld world) {
        this.world = world;
    }

    @Override
    public VoxelType createVoxelType(SemanticType semanticType) {
        return switch (semanticType) {
            case GRASS  -> new TestingVoxelType(this.world, "grass");
            case STONE  -> new TestingVoxelType(this.world, "stone");
            case DIRT   -> new TestingVoxelType(this.world, "dirt");
            case COBBLE -> new TestingVoxelType(this.world, "cobble");
            case BRICK  -> new TestingVoxelType(this.world, "brick");
            case WATER  -> new TestingVoxelType(this.world, "water");
            default     -> new TestingVoxelType(this.world, "air");
        };
    }
}

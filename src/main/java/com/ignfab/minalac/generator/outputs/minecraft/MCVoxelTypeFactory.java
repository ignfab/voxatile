package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;

/**
 * Factory for creating {@link MCVoxelType}.
 */
public class MCVoxelTypeFactory implements VoxelTypeFactory {
    private final MCVoxelWorld world;

    /**
     * Constructs a new {@code MCVoxelTypeFactory}.
     * The created voxels will be only able to be placed in the specified world.
     *
     * @param world the {@link MCVoxelWorld} from which the created voxels will be associated
     */
    public MCVoxelTypeFactory(MCVoxelWorld world) {
        this.world = world;
    }

    /**
     * Creates a new {@link MCVoxelType} corresponding to the provided {@link SemanticType}.
     * The created voxels are associated with this factory's world.
     *
     * @param semanticType the semantic type of the voxel to be created
     * @return the corresponding {@link MCVoxelType}
     */
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
            case LEAF -> "minecraft:oak_leaves";
            case WOOD -> "minecraft:oak_wood";
        });
    }
}

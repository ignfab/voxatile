package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.outputs.minetest.voxelType.MTSimpleVoxelType;

/**
 * Factory for creating {@link MTVoxelType}.
 */
public class MTVoxelTypeFactory implements VoxelTypeFactory {
    private final MTVoxelWorld world;

    /**
     * Constructs a new {@code MTVoxelTypeFactory}.
     * The created voxels will be only able to be placed in the specified world.
     *
     * @param world the {@link MTVoxelWorld} from which the created voxels will be associated
     */
    public MTVoxelTypeFactory(MTVoxelWorld world) {
        this.world = world;
    }

    /**
     * Creates a new {@link MTSimpleVoxelType} corresponding to the provided {@link SemanticType}.
     * The created voxels are associated with this factory's world.
     *
     * @param semanticType the semantic type of the voxel to be created
     * @return the corresponding {@link MTSimpleVoxelType}
     */
    @Override
    public VoxelType createVoxelType(SemanticType semanticType) {
        // Node string can be found on https://wiki.minetest.net/Games/Minetest_Game/Nodes
        return switch (semanticType) {
            case GRASS -> new MTSimpleVoxelType(this.world, "default:dirt_with_grass");
            case STONE -> new MTSimpleVoxelType(this.world, "default:stone");
            case DIRT -> new MTSimpleVoxelType(this.world, "default:dirt");
            case COBBLE -> new MTSimpleVoxelType(this.world, "default:cobble");
            case BRICK -> new MTSimpleVoxelType(this.world, "default:stonebrick");
            case WATER -> new MTSimpleVoxelType(this.world, "default:water_source");
            case LEAF -> new MTSimpleVoxelType(this.world, "default:leaves");
            case WOOD -> new MTSimpleVoxelType(this.world, "default:wood");
            default -> new MTSimpleVoxelType(this.world, "air");
        };
    }
}

package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.outputs.minetest.mod.ColoredVoxelsLuaMod;
import com.ignfab.minalac.generator.outputs.minetest.mod.FloatingTextsLuaMod;
import com.ignfab.minalac.generator.world.EntityType;
import com.ignfab.minalac.generator.world.MultilineTextEntityVerticalAnchor;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.outputs.minetest.voxelType.MTSimpleVoxelType;

/**
 * Factory for creating {@link MTVoxelType}.
 */
public class MTVoxelTypeFactory implements VoxelTypeFactory {
    private final MTVoxelWorld world;
    private final ColoredVoxelsLuaMod colors = new ColoredVoxelsLuaMod();
    private final FloatingTextsLuaMod texts = new FloatingTextsLuaMod();

    /**
     * Constructs a new {@code MTVoxelTypeFactory}.
     * The created voxels will be only able to be placed in the specified world.
     *
     * @param world the {@link MTVoxelWorld} from which the created voxels will be associated
     */
    public MTVoxelTypeFactory(MTVoxelWorld world) {
        this.world = world;
        world.registerMod("colors", colors);
        world.registerMod("texts", texts);
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
            case CONCRETE -> new MTSimpleVoxelType(this.world, "default:obsidian");
            case VEGETATION -> new MTSimpleVoxelType(this.world, "default:leaves");
            case PATH -> new MTSimpleVoxelType(this.world, "default:dry_dirt");

            case WHITE -> createColor(0xFFFFFF, false);
            case ORANGE -> createColor(0xFFA500, false);
            case MAGENTA -> createColor(0xFF00FF, false);
            case LIGHT_BLUE -> createColor(0xADD8E6, false);
            case YELLOW -> createColor(0xFFFF00, false);
            case LIME -> createColor(0x00FF00, false);
            case PINK -> createColor(0xFFC0CB, false);
            case GRAY -> createColor(0x808080, false);
            case LIGHT_GRAY -> createColor(0xD3D3D3, false);
            case CYAN -> createColor(0x00FFFF, false);
            case PURPLE -> createColor(0x800080, false);
            case BLUE -> createColor(0x0000FF, false);
            case BROWN -> createColor(0x663300, false);
            case GREEN -> createColor(0x008000, false);
            case RED -> createColor(0xFF0000, false);
            case BLACK -> createColor(0x000000, false);

            default -> new MTSimpleVoxelType(this.world, "air");
        };
    }

    @Override
    public VoxelType createColor(int rgb) {
        return createColor(rgb, true);
    }

    private VoxelType createColor(int rgb, boolean downsample) {
        int color = colors.registerColor(rgb, downsample ? 32 : 0);
        return new MTSimpleVoxelType(world, colors.nodeId(color));
    }

    @Override
    public EntityType createText(String text, MultilineTextEntityVerticalAnchor anchor) {
        return new MTTextEntityType(texts, text);
    }
}

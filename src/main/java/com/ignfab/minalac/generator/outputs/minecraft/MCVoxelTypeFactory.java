package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.MultilineTextEntityVerticalAnchor;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;
import com.ignfab.minalac.generator.world.VoxelTypeIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating {@link MCVoxelType}.
 */
public class MCVoxelTypeFactory implements VoxelTypeFactory {
    private final MCVoxelWorld world;

    private static final Map<Integer, String> colorPalette = new HashMap<>();
    static {
        colorPalette.put(0xFFFFFF, "white_concrete");
        colorPalette.put(0xE26100, "orange_concrete");
        colorPalette.put(0xAB2EA1, "magenta_concrete");
        colorPalette.put(0x1F8BC9, "light_blue_concrete");
        colorPalette.put(0xF2B00D, "yellow_concrete");
        colorPalette.put(0x5DA910, "lime_concrete");
        colorPalette.put(0xD76590, "pink_concrete");
        colorPalette.put(0x33373B, "gray_concrete");
        colorPalette.put(0x7F7F75, "light_gray_concrete");
        colorPalette.put(0x0F798A, "cyan_concrete");
        colorPalette.put(0x64199D, "purple_concrete");
        colorPalette.put(0x282A90, "blue_concrete");
        colorPalette.put(0x613A1A, "brown_concrete");
        colorPalette.put(0x485B1F, "green_concrete");
        colorPalette.put(0x901E1E, "red_concrete");
        colorPalette.put(0x000000, "black_concrete");
    }

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
            case CONCRETE -> "minecraft:black_wool";
            case VEGETATION -> "minecraft:oak_leaves";
            case PATH -> "minecraft:coarse_dirt";
            case CROPS -> "minecraft:wheat";

            // COLORS
            case WHITE -> "minecraft:white_concrete";
            case ORANGE -> "minecraft:orange_concrete";
            case MAGENTA -> "minecraft:magenta_concrete";
            case LIGHT_BLUE -> "minecraft:light_blue_concrete";
            case YELLOW -> "minecraft:yellow_concrete";
            case LIME -> "minecraft:lime_concrete";
            case PINK -> "minecraft:pink_concrete";
            case GRAY -> "minecraft:gray_concrete";
            case LIGHT_GRAY -> "minecraft:light_gray_concrete";
            case CYAN -> "minecraft:cyan_concrete";
            case PURPLE -> "minecraft:purple_concrete";
            case BLUE -> "minecraft:blue_concrete";
            case BROWN -> "minecraft:brown_concrete";
            case GREEN -> "minecraft:green_concrete";
            case RED -> "minecraft:red_concrete";
            case BLACK -> "minecraft:black_concrete";
        }, switch (semanticType) {
            case VEGETATION -> Map.of("persistent", "true");
            case CROPS -> Map.of("age", "7");
            default -> null;
        });
    }

    @Override
    public VoxelType createColor(int rgb) {
        String voxel = colorPalette.get(rgb);
        if (voxel != null)
            // Exact match found
            return new MCVoxelType(world, voxel);

        // No exact match, needs down-sampling
        String closest = null;
        int minDist = Integer.MAX_VALUE;

        for (Map.Entry<Integer, String> entry : colorPalette.entrySet()) {
            int c = entry.getKey();
            int dist = distance(rgb, c);
            if (closest == null || dist < minDist) {
                closest = entry.getValue();
                minDist = dist;
            }
        }

        return closest == null ? new VoxelTypeIgnore() : new MCVoxelType(world, closest);
    }

    private int distance(int c1, int c2) {
        int diffR = (c1 & 0xFF) - (c2 & 0xFF);
        int diffG = ((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF);
        int diffB = ((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF);
        return diffR * diffR + diffG * diffG + diffB * diffB;
    }

    @Override
    public MCTextEntityType createText(String text, MultilineTextEntityVerticalAnchor anchor) {
        String[] lines = text.split("\n");
        if (lines.length == 1)
            return new MCTextEntityType(world, text);
        else
            return new MCMultilineTextEntityType(world, anchor, lines);
    }
}

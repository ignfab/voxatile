package com.ignfab.minalac.generator.world;

/**
 * An enum for the semantic representation of voxels.
 *
 * @see VoxelType
 * @see VoxelTypeFactory#createVoxelType(SemanticType)
 */
public enum SemanticType {
    /**
     * Represent a grass voxel.
     * Can be used to represent lawn areas of flat vegetation.
     */
    GRASS,
    /**
     * Represent a stone voxel.
     * Can be used to represent natural rock formation and underground terrain.
     */
    STONE,
    /**
     * Represent an empty voxel.
     * It is often not required to place air as it is the default value, but it can be used to delete an already placed voxel.
     * Some output formats may require at least one air voxels to be present in the area to consider it generated.
     */
    AIR,
    /**
     * Represent a fluid voxel (water).
     * Some output formats allow fluids to be at the same place as other voxels.
     */
    WATER,
    /**
     * Represent a dirt voxel.
     * Can be used under grass and some type of field.
     */
    DIRT,
    /**
     * Represent a cobblestone voxel.
     * Can be used to represent human-placed rocks or cobbles.
     */
    COBBLE,
    /**
     * Represent a brick voxel.
     * Can be used to represent a brick-built wall.
     */
    BRICK,
    /**
     * Represent a concrete voxel.
     * Can be used to represent a road.
     */
    CONCRETE,
    VEGETATION,
    PATH,
    CROPS,

    // COLORS
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK
}

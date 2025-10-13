package com.ignfab.minalac.generator.modules.minecraft;

import java.util.HashMap;
import java.util.Map;

import io.github.ensgijs.nbt.tag.CompoundTag;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mock class for {@link MinecraftVoxelTile} to simplify block placement tests.
 */
final class MinecraftVoxelTileMock extends MinecraftVoxelTile {
    private final Map<String, CompoundTag> blockStates = new HashMap<>();
    private final Map<String, CompoundTag> blockEntities = new HashMap<>();

    /**
     * Creates a new mock tile.
     * @param limits limits of the tile
     */
    MinecraftVoxelTileMock(WorldBBox3d limits) {
        super(null, limits);
    }

    @Override
    void setBlockState(int blockX, int blockY, int blockZ, CompoundTag block) {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        String key = key(blockX, blockY, blockZ);
        blockStates.put(key, block);
        blockEntities.remove(key);
    }

    @Override
    void addBlockEntity(int blockX, int blockY, int blockZ, CompoundTag block) {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        block.putInt("x", blockX);
        block.putInt("y", blockY);
        block.putInt("z", blockZ);
        blockEntities.put(key(blockX, blockY, blockZ), block);
    }

    /**
     * Asserts that the block state placed at given position matches the given one.
     * The position is in game coordinates.
     * @param blockX x-coordinate of the position
     * @param blockY y-coordinate of the position
     * @param blockZ z-coordinate of the position
     * @param block block state to match
     */
    public void assertBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag block) {
        CompoundTag state = blockStates.get(key(blockX, blockY, blockZ));
        assertEquals(block, state, "Block state mismatch at (%d, %d, %d)".formatted(blockX, blockY, blockZ));
    }

    /**
     * Asserts that the block entity placed at given position matches the given one.
     * The position is in game coordinates.
     * @param blockX x-coordinate of the position
     * @param blockY y-coordinate of the position
     * @param blockZ z-coordinate of the position
     * @param block block entity to match
     */
    public void assertBlockEntityAt(int blockX, int blockY, int blockZ, CompoundTag block) {
        CompoundTag entity = blockEntities.get(key(blockX, blockY, blockZ));
        assertEquals(block, entity, "Block entity mismatch at (%d, %d, %d)".formatted(blockX, blockY, blockZ));
    }

    private String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }
}

package com.ignfab.minalac.generator.modules.minecraft;

import java.io.File;
import java.io.IOException;

import io.github.ensgijs.nbt.mca.TerrainChunk;
import io.github.ensgijs.nbt.mca.io.McaFileHelpers;
import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * Implementation of {@link VoxelTile} for Minecraft.
 */
public class MinecraftVoxelTile extends VoxelTile {
    private final File destination;

    private final Int2ObjectMap<Region> regions = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    /**
     * Creates a new {@code MinecraftVoxelTile}.
     *
     * @param destination Destination directory (must point to existing "region" directory)
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public MinecraftVoxelTile(File destination, WorldBBox3d limits) {
        super(limits);
        this.destination = destination;
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minecraft.
     * @throws MapWriteException if a {@link Region} cannot be {@link Region#save(File) saved}
     */
    @Override
    public void save() throws MapWriteException {
        if (destination == null)
            return; // Save disabled if null destination

        for (Region region : regions.values()) {
            try {
                region.save(destination);
            } catch (IOException e) {
                throw new MapWriteException("Unable to save region " + region.getFileName(), e);
            }
        }
    }

    // In-Game coords
    /* package-private */ void setBlockState(int blockX, int blockY, int blockZ, CompoundTag block) {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        TerrainChunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(McaFileHelpers.blockToChunk(blockX), McaFileHelpers.blockToChunk(blockZ));
        if (!chunk.containsSection(blockY / 16)) {
            synchronized (chunk) {
                if (!chunk.containsSection(blockY / 16))
                    chunk.createSection(blockY / 16);
            }
        }
        synchronized (chunk) {
            // This call is synchronized as a workaround because the NBT library is not thread-safe.
            // This is a performance-killer!
            // The only part that really needs synchronization is inside io.github.ensgijs.nbt.mca.util.PalettizedCuboid:
            // When adding a block, if the palette must be modified, it should be synchronized.
            // See: https://github.com/ens-gijs/NBT/issues/7

            chunk.setBlockAt(
                McaFileHelpers.blockAbsoluteToChunkRelative(blockX),
                blockY,
                McaFileHelpers.blockAbsoluteToChunkRelative(blockZ),
                block
            );
        }
        clearBlockEntity(blockX, blockY, blockZ); // TODO This negatively affects performances and should be optimized!
        // (In-Game coords to world coords) X/Z/-Y => X/Y/Z
        updateHeightmaps(blockX, -(blockZ + 1), blockY);
    }

    // In-Game coords
    /* package-private */ void addBlockEntity(int blockX, int blockY, int blockZ, CompoundTag block)  {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        TerrainChunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(McaFileHelpers.blockToChunk(blockX), McaFileHelpers.blockToChunk(blockZ));
        ListTag<CompoundTag> blockEntities = chunk.getTileEntities();
        if (blockEntities == null) {
            synchronized (chunk) {
                blockEntities = chunk.getTileEntities();
                if (blockEntities == null) {
                    blockEntities = new ListTag<>(CompoundTag.class);
                    chunk.setTileEntities(blockEntities);
                }
            }
        }
        block.putInt("x", blockX);
        block.putInt("y", blockY);
        block.putInt("z", blockZ);
        synchronized (blockEntities) {
            blockEntities.add(block);
        }
    }

    private void clearBlockEntity(int blockX, int blockY, int blockZ) {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        TerrainChunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(McaFileHelpers.blockToChunk(blockX), McaFileHelpers.blockToChunk(blockZ));
        ListTag<CompoundTag> blockEntities = chunk.getTileEntities();
        if (blockEntities == null)
            return;
        synchronized (blockEntities) {
            blockEntities.removeIf(blockEntity -> MinecraftHelpers.xyzEquals(blockEntity, blockX, blockY, blockZ));
        }
    }

    // In-Game coords
    private Region getOrCreateRegion(int blockX, int blockZ)  {
        return regions.computeIfAbsent(Region.computeKeyFromBlock(blockX, blockZ), Region::new);
    }

    // In-Game coords
    /* package-private */ boolean isOutOfLimits(int blockX, int blockY, int blockZ) {
        // (In-Game coords to world coords) X/Z/-Y => X/Y/Z
        return !limits().contains(blockX, -(blockZ + 1), blockY);
    }

    /**
     * {@inheritDoc}
     * The returned voxel is not necessarily one placed using {@link Placeable#place}.
     * It may be an air block created when the world is initialized.
     * <p>
     * If you try to get a voxel outside the tile limits, it will return {@link MinecraftVoxel#DEFAULT_VOXEL}.
     */
    @Override
    public Placeable getVoxel(int x, int y, int z) {
        // (World coords to In-Game coords) X/Y/Z => X/Z/-Y-1
        int blockX = x;
        int blockY = z;
        int blockZ = -y - 1;

        Region region = regions.get(Region.computeKeyFromBlock(blockX, blockZ));
        if (region == null)
            return MinecraftVoxel.DEFAULT_VOXEL;

        CompoundTag blockState = region.getBlockState(blockX, blockY, blockZ);
        if (blockState == null)
            return MinecraftVoxel.DEFAULT_VOXEL;
        MinecraftVoxel voxel = MinecraftVoxel.fromBlockState(blockState);

        CompoundTag blockEntity = region.getBlockEntity(blockX, blockY, blockZ);
        return blockEntity == null ? voxel : MinecraftBlockEntityVoxel.fromBlockEntity(blockEntity, voxel);
    }
}

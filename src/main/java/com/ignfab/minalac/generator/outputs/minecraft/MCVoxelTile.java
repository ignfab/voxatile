package com.ignfab.minalac.generator.outputs.minecraft;

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
public class MCVoxelTile extends VoxelTile {
    private final File destination;

    private final Int2ObjectMap<Region> regions = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    public static final int Z_OFFSET = -64;

    /**
     * Creates a new {@code MCVoxelTile}.
     *
     * @param destination Destination directory (must point to existing "region" directory)
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public MCVoxelTile(File destination, WorldBBox3d limits) {
        super(limits.to2d().to3d(limits.minZ() - Z_OFFSET, limits.sizeZ()));
        this.destination = destination;
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minecraft.
     */
    @Override
    public void save() throws MapWriteException {
        for (Region region : regions.values()) {
            try {
                region.save(destination);
            } catch (IOException e) {
                throw new MapWriteException("Unable to save region " + region.getFileName(), e);
            }
        }
    }

    // In-Game coords
    /* package-private */ synchronized void setBlockState(int blockX, int blockY, int blockZ, CompoundTag block) {
        // This method is synchronized as a workaround because the Querz library is not thread-safe.
        // This is a performance-killer!
        // The only part that really needs synchronization is inside nbt.querz.mca.Section:
        // During a palette update (adjustBlockStateBits), it should block, until operation
        // is complete, any other thread trying to add a block to the palette (addToPalette,
        // after the check for existing block inside palette).

        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        TerrainChunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(McaFileHelpers.blockToChunk(blockX), McaFileHelpers.blockToChunk(blockZ));
        if (!chunk.containsSection(blockY / 16))
            chunk.createSection(blockY / 16);
        chunk.setBlockAt(
            McaFileHelpers.blockAbsoluteToChunkRelative(blockX),
            blockY,
            McaFileHelpers.blockAbsoluteToChunkRelative(blockZ),
            block
        );
        clearBlockEntity(blockX, blockY, blockZ); // TODO This negatively affects performances and should be optimized!
        // (In-Game coords to world coords) X/Z/-Y => X/Y/Z
        updateHeightmaps(blockX, -(blockZ + 1), blockY - Z_OFFSET);
    }

    // In-Game coords
    /* package-private */ void addBlockEntity(int blockX, int blockY, int blockZ, CompoundTag block)  {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        TerrainChunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(McaFileHelpers.blockToChunk(blockX), McaFileHelpers.blockToChunk(blockZ));
        ListTag<CompoundTag> blockEntities = chunk.getTileEntities();
        if (blockEntities == null) {
            blockEntities = new ListTag<>(CompoundTag.class);
            chunk.setTileEntities(blockEntities);
        }
        block.putInt("x", blockX);
        block.putInt("y", blockY);
        block.putInt("z", blockZ);
        blockEntities.add(block);
    }

    private void clearBlockEntity(int blockX, int blockY, int blockZ) {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        TerrainChunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(McaFileHelpers.blockToChunk(blockX), McaFileHelpers.blockToChunk(blockZ));
        ListTag<CompoundTag> blockEntities = chunk.getTileEntities();
        if (blockEntities == null)
            return;
        blockEntities.removeIf(blockEntity -> MCHelpers.xyzEquals(blockEntity, blockX, blockY, blockZ));
    }

    // In-Game coords
    private Region getOrCreateRegion(int blockX, int blockZ)  {
        return regions.computeIfAbsent(Region.computeKeyFromBlock(blockX, blockZ), Region::new);
    }

    // In-Game coords
    /* package-private */ boolean isOutOfLimits(int blockX, int blockY, int blockZ) {
        // (In-Game coords to world coords) X/Z/-Y => X/Y/Z
        return !limits().contains(blockX, -(blockZ + 1), blockY - Z_OFFSET);
    }

    /**
     * {@inheritDoc}
     * The returned voxel is not necessarily one placed using {@link Placeable#place}.
     * It may be an air block created when a {@link Region} is initialized.
     */
    @Override
    public Placeable getVoxel(int x, int y, int z) {
        // (World coords to In-Game coords) X/Y/Z => X/Z/-Y-1
        int blockX = x;
        int blockY = z + Z_OFFSET;
        int blockZ = -y - 1;

        Region region = regions.get(Region.computeKeyFromBlock(blockX, blockZ));
        if (region == null)
            return MCVoxel.DEFAULTVOXEL;

        CompoundTag blockState = region.getBlockState(blockX, blockY, blockZ);
        if (blockState == null)
            return MCVoxel.DEFAULTVOXEL;
        MCVoxel voxel = MCVoxel.fromBlockState(blockState);

        CompoundTag blockEntity = region.getBlockEntity(blockX, blockY, blockZ);
        return blockEntity == null ? voxel : MCBlockEntityVoxel.fromBlockEntity(blockEntity, voxel);
    }
}

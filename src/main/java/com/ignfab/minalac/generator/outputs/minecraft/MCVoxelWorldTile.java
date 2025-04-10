package com.ignfab.minalac.generator.outputs.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.querz.mca.Chunk;
import net.querz.mca.MCAUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * Implementation of {@link VoxelWorldTile} for Minecraft.
 */
public class MCVoxelWorldTile extends VoxelWorldTile {
    private final Map<Integer, Region> regions = new HashMap<>();

    /**
     * Creates a new {@code MCVoxelWorldTile}.
     *
     * @param world {@link MCVoxelWorld} of which this tile is a part
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public MCVoxelWorldTile(MCVoxelWorld world, WorldBBox3d limits) {
        super(world, limits);
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minecraft.
     */
    @Override
    public void save(File destination) throws MapWriteException {
        File regionDirectory = new File(destination, "region");
        regionDirectory.mkdir();
        for (Region region : regions.values()) {
            try {
                region.save(regionDirectory);
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
        getOrCreateRegion(blockX, blockZ).file().setBlockStateAt(blockX, blockY, blockZ, block, false);
    }

    // In-Game coords
    /* package-private */ void addBlockEntity(int blockX, int blockY, int blockZ, CompoundTag block)  {
        if (isOutOfLimits(blockX, blockY, blockZ)) return;
        Chunk chunk = getOrCreateRegion(blockX, blockZ).getOrCreateChunk(MCAUtil.blockToChunk(blockX), MCAUtil.blockToChunk(blockZ));
        ListTag<CompoundTag> blockEntities = chunk.getTileEntities();
        if (blockEntities == null) {
            blockEntities = new ListTag<>(CompoundTag.class);
            chunk.setTileEntities(blockEntities);
        }
        blockEntities.add(block);
    }

    // In-Game coords
    private Region getOrCreateRegion(int blockX, int blockZ)  {
        int regionX = MCAUtil.blockToRegion(blockX);
        int regionZ = MCAUtil.blockToRegion(blockZ);
        int key = computeRegionKey(regionX, regionZ);
        Region region = regions.get(key);
        if (region == null) {
            region = new Region(regionX, regionZ);
            regions.put(key, region);
        }
        return region;
    }

    // In-Game coords
    private int computeRegionKey(int regionX, int regionZ) {
        return (regionX << 16) | (regionZ & 0xFFFF);
    }

    // In-Game coords
    /* package-private */ boolean isOutOfLimits(int blockX, int blockY, int blockZ) {
        // (In-Game coords to world coords) X/Z/-Y => X/Y/Z
        return !limits().contains(blockX, -(blockZ + 1), blockY);
    }
}

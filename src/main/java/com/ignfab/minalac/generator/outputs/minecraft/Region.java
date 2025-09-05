package com.ignfab.minalac.generator.outputs.minecraft;

import java.io.File;
import java.io.IOException;

import net.querz.mca.Chunk;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a Minecraft region.
 * A region consists of 32x32 chunks and each chunk is 16 blocks wide by 16 blocks long.
 *
 * @param regionX the x-coordinate of this region
 * @param regionZ the y-coordinate of this region
 * @param file the {@link MCAFile} representing the file used by Minecraft
 * @see <a href="https://minecraft.wiki/w/Region_file_format">Region (Minecraft Wiki)</a>
 * @see <a href="https://minecraft.wiki/w/Chunk">Chunk (Minecraft Wiki)</a>
 */
public record Region(int regionX, int regionZ, MCAFile file) {

    /**
     * Region size, used for tiling.
     */
    public static final int SIZE = 512;

    /**
     * Constructs a new {@link Region}.
     *
     * @param regionX the x-coordinate of this region
     * @param regionZ the y-coordinate of this region
     */
    public Region(int regionX, int regionZ) {
        this(regionX, regionZ, new MCAFile(regionX, regionZ));
    }

    /**
     * Constructs a new {@link Region}.
     *
     * @param regionKey the int-packed key of this region
     * @see #computeKey(int, int)
     */
    public Region(int regionKey) {
        this(keyToRegionX(regionKey), keyToRegionZ(regionKey));
    }

    /**
     * Returns the {@link Chunk} at the specified coordinates or creates it if it doesn't exist.
     *
     * @param chunkX the x-coordinate of the chunk
     * @param chunkZ the z-coordinate of the chunk
     * @return the corresponding chunk
     */
    public Chunk getOrCreateChunk(int chunkX, int chunkZ) {
        Chunk chunk = file.getChunk(chunkX, chunkZ);
        if (chunk == null) {
            chunk = Chunk.newChunk();
            file.setChunk(chunkX, chunkZ, chunk);
        }
        return chunk;
    }

    /**
     * {@return the filename of this region}
     */
    public String getFileName() {
        return MCAUtil.createNameFromRegionLocation(regionX, regionZ);
    }

    /**
     * Saves the region in the specified directory.
     *
     * @param regionDirectory the directory of the region file
     * @throws IOException if something goes wrong while saving
     */
    public void save(File regionDirectory) throws IOException {
        file.cleanupPalettesAndBlockStates();
        for (int i = 0; i < 1024; i++) {
            Chunk chunk = file.getChunk(i);
            if (chunk != null)
                chunk.setStatus("minecraft:full");
        }
        MCAUtil.write(file, new File(regionDirectory, getFileName()), true);
    }

    /**
     * Loads a {@code Region} from a file region.
     *
     * @param regionsDirectory the directory where the region file is located
     * @param regionX the x-value of the location of the region
     * @param regionZ the z-value of the location of the region
     * @return a new {@link Region} corresponding to the file region
     * @throws IOException if something goes wrong while deserializing the file region
     */
    public static Region load(String regionsDirectory, int regionX, int regionZ) throws IOException {
        return new Region(regionX, regionZ, MCAUtil.read(new File(regionsDirectory, MCAUtil.createNameFromRegionLocation(regionX, regionZ))));
    }

    /**
     * Returns a block as a new {@link MCVoxel}.
     *
     * @param blockX the in-game x-coordinate
     * @param blockY the in-game y-coordinate
     * @param blockZ the in-game z-coordinate
     * @return the corresponding voxel, or {@code null} if it doesn't exist
     */
    public MCVoxel getBlock(int blockX, int blockY, int blockZ) {
        CompoundTag block = file().getBlockStateAt(blockX, blockY, blockZ);
        return (block == null) ? null : MCVoxel.fromBlockState(block);
    }

    /**
     * Computes the int-packed key of the region containing the blocks with given x/z.
     * @param blockX the x-coordinate of the block
     * @param blockZ the z-coordinate of the block
     * @return the region key
     */
    public static int computeKeyFromBlock(int blockX, int blockZ) {
        return computeKey(MCAUtil.blockToRegion(blockX), MCAUtil.blockToRegion(blockZ));
    }

    /**
     * Computes the int-packed key of the region with given x/z.
     * @param regionX the x-coordinate of the region
     * @param regionZ the z-coordinate of the region
     * @return the region key
     */
    public static int computeKey(int regionX, int regionZ) {
        return (regionX << 16) | (regionZ & 0xFFFF);
    }

    /**
     * Extracts the x-coordinate of the region from its key.
     * @param regionKey the region key
     * @return the x-coordinate of the region
     */
    public static int keyToRegionX(int regionKey) {
        return (short) ((regionKey >> 16) & 0xFFFF); // cast to short to properly restore sign
    }

    /**
     * Extracts the z-coordinate of the region from its key.
     * @param regionKey the region key
     * @return the z-coordinate of the region
     */
    public static int keyToRegionZ(int regionKey) {
        return (short) (regionKey & 0xFFFF); // cast to short to properly restore sign
    }
}

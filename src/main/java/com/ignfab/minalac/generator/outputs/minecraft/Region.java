package com.ignfab.minalac.generator.outputs.minecraft;

import net.querz.mca.Chunk;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;

import java.io.File;
import java.io.IOException;

public record Region(int regionX, int regionZ, MCAFile file) {
    public Region(int regionX, int regionZ) {
        this(regionX, regionZ, new MCAFile(regionX, regionZ));
    }

    public Chunk getOrCreateChunk(int chunkX, int chunkZ) {
        Chunk chunk = file.getChunk(chunkX, chunkZ);
        if (chunk == null)
            file.setChunk(chunkX, chunkZ, chunk = Chunk.newChunk());
        return chunk;
    }

    public String getFileName() {
        return MCAUtil.createNameFromRegionLocation(regionX, regionZ);
    }

    public void save(File regionDirectory) throws IOException {
        file.cleanupPalettesAndBlockStates();
        for (int i = 0; i < 1024; i++) {
            Chunk chunk = file.getChunk(i);
            if (chunk != null)
                chunk.setStatus("minecraft:full");
        }
        MCAUtil.write(file, new File(regionDirectory, getFileName()), true);
    }

    public static Region load(String regionsDirectory, int regionX, int regionZ) throws IOException {
        return new Region(regionX, regionZ, MCAUtil.read(new File(regionsDirectory, MCAUtil.createNameFromRegionLocation(regionX, regionZ))));
    }
}

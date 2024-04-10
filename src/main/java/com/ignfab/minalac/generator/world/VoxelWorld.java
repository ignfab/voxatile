package com.ignfab.minalac.generator.world;

public interface VoxelWorld {
    VoxelTypeFactory getFactory();

    void save(String directoryFullPath) throws MapWriteException;
}
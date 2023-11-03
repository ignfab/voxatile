package com.ignfab.minalac.generator;

public interface VoxelWorld {
    VoxelTypeFactory getFactory();

    void save(String directoryFullPath) throws MapWriteException;
}
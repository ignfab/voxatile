package com.ignfab.minalac.generator.world;

import java.io.File;

public interface VoxelWorld {
    VoxelTypeFactory getFactory();

    VoxelWorldMetadata getMetadata();

    void save(File destination) throws MapWriteException;
}

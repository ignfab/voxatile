package com.ignfab.minalac.generator;

public interface VoxelType {
    //TODO find a convention to explicitly define the coordinates
    void place(int x, int y, int z) throws OutOfWorldException;
}
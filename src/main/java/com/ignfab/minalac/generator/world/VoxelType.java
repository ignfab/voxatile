package com.ignfab.minalac.generator.world;

public interface VoxelType {
    //TODO : add the javadoc that explicitly define the coordinate system used + change the QuickStart.md accordingly
    void place(int x, int y, int z) throws OutOfWorldException;
}
package com.ignfab.minalac.generator;

public interface VoxelTypeFactory {
    VoxelType createVoxelType(SemanticType semanticType);
    //TODO : implementation of a method that takes into account advanced voxel creation
    //VoxelType createVoxelType(SemanticType semanticType, parameters);
}
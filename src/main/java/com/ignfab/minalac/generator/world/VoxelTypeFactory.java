package com.ignfab.minalac.generator.world;

/**
 * Factory for creating {@link VoxelType}.
 */
public interface VoxelTypeFactory {
    /**
     * Creates a new {@link VoxelType} corresponding to the provided {@link SemanticType}.
     *
     * @param semanticType the semantic type of the voxel to be created
     * @return the corresponding {@link VoxelType}
     */
    VoxelType createVoxelType(SemanticType semanticType);
    //TODO : implementation of a method that takes into account advanced voxel creation
    //VoxelType createVoxelType(SemanticType semanticType, parameters);
}

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
    // TODO : implementation of a method that takes into account advanced voxel creation
    // VoxelType createVoxelType(SemanticType semanticType, parameters);

    /**
     * Creates a new {@link EntityType} representing a floating text.
     * The text may be multiline, using the {@code \n} line separator.
     * In that case, the vertical anchor will default to
     * {@link MultilineTextEntityVerticalAnchor#MIDDLE middle}.
     *
     * @param text the text content of the entity to be created
     * @return the created {@link EntityType}
     */
    default EntityType createText(String text) {
        return createText(text, MultilineTextEntityVerticalAnchor.MIDDLE);
    }

    /**
     * Creates a new {@link EntityType} representing a floating text.
     * The text may be multiline, using the {@code \n} line separator.
     *
     * @param text the text content of the entity to be created
     * @param anchor the vertical anchor in case of multiline text
     * @return the created {@link EntityType}
     */
    default EntityType createText(String text, MultilineTextEntityVerticalAnchor anchor) {
        // TODO When implemented in Minetest, this default throw should be removed
        throw new UnsupportedOperationException();
    }
}

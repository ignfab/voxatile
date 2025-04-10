package com.ignfab.minalac.generator.generation.heightmaps;

/**
 * Spec for a readable heightmap. This is an abstract class to be derivated for each type of heightmap.
 * <p>
 * {@link ReadableHeightmapSpec#create(HeightmapStore)} should contain code for corresponding heightmap creation.
 */
public abstract class ReadableHeightmapSpec {
    /**
     * Creates an item from specs.
     *
     * @param store store to use for item creation
     * @return created item
     *
     * This method is only intended to be used from {@link HeightmapStore#get(ReadableHeightmapSpec)}.
     */
    protected abstract ReadableHeightmap create(HeightmapStore store);
}

package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.generation.GenerationTile;

/**
 * An unbound 2d readable heightmap (in voxel world units) that should be bound to a {@link GenerationTile} before being used.
 */
public interface UnboundReadableHeightmap {

    /**
     * Binds this {@link UnboundReadableHeightmap} to a {@link GenerationTile} to produce a usable {@link ReadableHeightmap}.
     *
     * @param tile Tile to bind the heightmap to
     * @return Bound heightmap
     */
    ReadableHeightmap bind(GenerationTile tile);

}

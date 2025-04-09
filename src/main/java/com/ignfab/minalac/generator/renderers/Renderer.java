package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A renderer capable of rendering something in an area.
 */
public interface Renderer {
    /**
     * Performs rendering.
     *
     * @param world world to render into
     */
    void render(VoxelWorld world);
}

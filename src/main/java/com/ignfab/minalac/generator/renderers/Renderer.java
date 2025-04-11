package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.GenerationTile;

/**
 * A renderer capable of rendering something in an area.
 */
public interface Renderer {
    /**
     * Performs rendering.
     *
     * @param tile tile to render into
     */
    void render(GenerationTile tile);
}
